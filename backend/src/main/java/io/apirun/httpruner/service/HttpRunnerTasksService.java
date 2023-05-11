package io.apirun.httpruner.service;

import io.apirun.base.domain.HttprunnerTask;
import io.apirun.base.domain.NodeRegisterInfo;
import io.apirun.base.mapper.HttprunnerTaskMapper;
import io.apirun.base.mapper.NodeRegisterInfoMapper;
import io.apirun.base.mapper.ext.ExtHttprunnerTaskMapper;
import io.apirun.base.mapper.ext.ExtNodeRegisterInfoMapper;
import io.apirun.common.Qcm.config.IQConf;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.IPUtil;
import io.apirun.commons.utils.LogUtil;
import io.apirun.commons.utils.PojoUtil;
import io.apirun.config.KafkaProperties;
import io.apirun.httpruner.dto.*;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HttpRunnerTasksService {

    @Value("${kafka.task.topic}")
    private String topic;

    @Value("${kafka.tasklog.topic}")
    private String logTopic;

    @Value("${server.port}")
    private String serverPort;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Resource
    HttprunnerTaskMapper httprunnerTaskMapper;

    @Resource
    IQConf iqConf;

    @Resource
    ExtHttprunnerTaskMapper extHttprunnerTaskMapper;

    @Resource
    NodeRegisterInfoMapper nodeRegisterInfoMapper;

    @Resource
    ExtNodeRegisterInfoMapper extNodeRegisterInfoMapper;

    @Resource
    KafkaTemplate<String,Object> kafkaTemplate;

    /**
     * 发送任务到node节点
     * @param task
     */
    public void sendHttpRunnerTask(HttprunnerTask task){
//        task.setTaskStatus(HttpRunnerTaskStatus.SENDING.toString());
        //选取节点
        String nodeId = chooseNodeForTask(task);
        if(nodeId == null){
            LogUtil.error("当前无可用节点");
            MSException.throwException("当前无可用节点");
        }else {
            task.setNodeId(nodeId);
        }
        //判断当前任务数量是否达到负载
//        NodeRegisterInfo nodeRegisterInfo = nodeRegisterInfoMapper.selectByPrimaryKey(task.getNodeId());

        //组装NODE节点需要的信息
        SendMsgToNodeDto infoForNode = null;
        try {
            infoForNode = createInfoForNode(task);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.error("获取任务执行结束回调失败，本机地址获取失败");
            MSException.throwException("获取任务执行结束回调失败，本机地址获取失败");
        }
        String taskInfo = PojoUtil.toJson(infoForNode);
        LogUtil.info("往消息队列发送的任务："+taskInfo);
        kafkaTemplate.send(topic, taskInfo).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable e) {
                LogUtil.error("向kafka发送消息失败: "+e.getMessage());
                MSException.throwException("向kafka发送消息失败: "+e.getMessage());

            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                LogUtil.info("向kafka发送消息成功："+result.getRecordMetadata().topic() + "-"
                        + result.getRecordMetadata().partition() + "-" + result.getRecordMetadata().offset());
                SendMsgToNodeDto task = PojoUtil.fromJson(result.getProducerRecord().value().toString(),SendMsgToNodeDto.class);
                //更新任务的状态为SENDED
                HttprunnerTask httprunnerTask = new HttprunnerTask();
                httprunnerTask.setId(task.getTaskId());
                httprunnerTask.setNodeId(task.getNodeId());
                httprunnerTask.setTaskStatus(HttpRunnerTaskStatus.SENDED.toString());
                httprunnerTask.setUtime(System.currentTimeMillis());
                httprunnerTaskMapper.updateByPrimaryKeySelective(httprunnerTask);
            }
        });
    }

    /**
     * 获取node节点的应答，并更新执行的任务数量
     * @param nodeId
     * @param taskId
     */
    @Transactional
    public void getResponseFromNode(String nodeId, String taskId){
        //判断任务是否需要执行
        HttprunnerTask taskInfo = httprunnerTaskMapper.selectByPrimaryKey(taskId);
        if(taskInfo == null){
            LogUtil.error("当前任务任务id无效："+taskId);
            MSException.throwException("当前任务任务id无效："+taskId);
        }
        if (taskInfo.getTaskStatus().equals(HttpRunnerTaskStatus.SENDED.toString())){
            taskInfo.setTaskStatus(HttpRunnerTaskStatus.EXECUTING.toString());
            taskInfo.setNodeId(nodeId);
            taskInfo.setUtime(System.currentTimeMillis());
        }else {
            LogUtil.error("当前任务不执行，状态为："+taskInfo.getTaskStatus());
            MSException.throwException("当前任务不执行，状态为："+taskInfo.getTaskStatus());
        }
        NodeRegisterInfo nodeRegisterInfo = nodeRegisterInfoMapper.selectByPrimaryKey(nodeId);
        if (nodeRegisterInfo.getNodeStatus().equals(NodeRegisterStatusDto.RUNNABLE.toString())){
            nodeRegisterInfo.setTotalTasks(nodeRegisterInfo.getTotalTasks()+1);
        }else {
            LogUtil.error("当前节点不可用："+nodeRegisterInfo.getNodeStatus());
            MSException.throwException("当前节点不可用："+nodeRegisterInfo.getNodeStatus());
        }
        try{
            httprunnerTaskMapper.updateByPrimaryKeySelective(taskInfo);
            nodeRegisterInfoMapper.updateByPrimaryKeySelective(nodeRegisterInfo);
        }catch (Exception e){
            LogUtil.error(": 接收节点response应答后，发生异常:"+e);
            MSException.throwException("接收节点response应答后，发生异常: "+e);
        }
    }

    /**
     * 获取node节点传回的关于任务结束或者异常状态信息
     */
    public void getHttpRunnerTaskResultStatus(HttpRunnerTaskResultRequest request){
        request.setUtime(System.currentTimeMillis());
        extHttprunnerTaskMapper.updateTaskStatus(request);
    }


    /**
     * 随机选择一个可用节点
     * @return
     */
    public String chooseNodeForTask(HttprunnerTask task){
        String res = null;
        List<NodeRegisterInfo> infoList = extNodeRegisterInfoMapper.listNodeByStatus(NodeRegisterStatusDto.RUNNABLE.toString());
        int size = infoList.size();
        if (infoList == null || size == 0){
            LogUtil.error("当前无任何可用节点可以运行任务");
            MSException.throwException("当前无任何可用节点可以运行任务");
        }
        if(size == 1){
            res = infoList.get(0).getNodeId();
        }else {
            int index = RandomUtils.nextInt(0,size);
            NodeRegisterInfo nodeRegisterInfo = infoList.get(index);
            //节点是原来节点或者任务数超载直接换一个节点
            List<String> invalidNode = new ArrayList<>();
            invalidNode.add(task.getNodeId());
            while(task.getNodeId() == nodeRegisterInfo.getNodeId() || nodeRegisterInfo.getMaxLoad()<= getTaskCountByNodeIdAndStatus(nodeRegisterInfo.getNodeId(),HttpRunnerTaskStatus.EXECUTING.toString())){
                if(!invalidNode.contains(nodeRegisterInfo.getNodeId())){
                    invalidNode.add(task.getNodeId());
                }
                //防止所有节点都满的时候，造成死循环
                if(infoList.size() == invalidNode.size()){
                    return res;
                }
                index=RandomUtils.nextInt(0,size);
                nodeRegisterInfo = infoList.get(index);
            }
            res = infoList.get(index).getNodeId();
        }
        return res;
    }

    /**
     * 组装发往node节点的消息
     * @param task
     * @return
     */
    public SendMsgToNodeDto createInfoForNode(HttprunnerTask task) throws UnknownHostException {
        SendMsgToNodeDto msg = new SendMsgToNodeDto();
        msg.setTaskId(task.getId());
        msg.setNodeId(task.getNodeId());
        msg.setDebugMode(task.getDebugMode());
        msg.setDotEnvPath(task.getDotEnvPath());
        msg.setExecCasePaths(task.getExecCasePaths());
        msg.setGitVersion(task.getGitVersion());
        //TODO image先写死后面进行优化
        msg.setImage("testrobot/qhttprunner:v2.0");
        msg.setHostsPath(task.getHostsPath());
        msg.setProjectId(task.getProjectId());
        msg.setDataCallbackUrl(getTaskDataCallbackUrl());
        KafkaDto kafkaDto = new KafkaDto();
        kafkaDto.setBootstrapServers(bootstrapServers);
        kafkaDto.setLogTopic(logTopic);
        msg.setKafkaDto(kafkaDto);
        return msg;
    }

    public String getTaskDataCallbackUrl() throws UnknownHostException {
        String ip = IPUtil.getLocalIP();
        return String.format("http://%s:%s/api/httprunner/result/summary", ip, serverPort);
    }

    /**
     * 获取需要执行的任务
     * @return
     */
    public List<HttprunnerTask> getNeedExecuteTask(){
        List<String> list = new ArrayList<>();
        list.add(HttpRunnerTaskStatus.CREATED.toString());
        return extHttprunnerTaskMapper.getNeedExecuteTask(list);
    }

    /**
     * 处理节点中节点ERROR的任务直接置为失败。
     * @return
     */
    public void doHttpRunnerTaskFailure(){
        List<NodeRegisterInfo> infoList = extNodeRegisterInfoMapper.listNodeInfo();
        List<HttprunnerTask> listTask = null;
        for(NodeRegisterInfo info : infoList){
            listTask=extHttprunnerTaskMapper.getTaskByNodeIdAndTaskStatus(info.getNodeId(), HttpRunnerTaskStatus.EXECUTING.toString());
            //如果节点已经是Error了，节点上的所有任务置为FAILURE
            if(info.getNodeStatus() == NodeRegisterStatusDto.ERROR.toString()){
                changeStatusForErrListTask(listTask,HttpRunnerTaskStatus.FAILURE.toString());
            }
        }
    }

    /**
     * 处理节点中超时的任务
     * @return
     */
    public void doHttpRunnerTaskTimeOut(){
        List<String> statusList = new ArrayList<>();
        statusList.add(NodeRegisterStatusDto.RUNNABLE.toString());
        statusList.add(NodeRegisterStatusDto.BUSY.toString());
        List<NodeRegisterInfo> infoList = extNodeRegisterInfoMapper.listNodeByStatusList(statusList);
        List<HttprunnerTask> listTask = null;
        for(NodeRegisterInfo info : infoList){
            listTask=extHttprunnerTaskMapper.getTaskByNodeIdAndTaskStatus(info.getNodeId(), HttpRunnerTaskStatus.EXECUTING.toString());
            for(HttprunnerTask task : listTask){
                if(System.currentTimeMillis() - task.getUtime() > iqConf.getHttpRunnerTaskTimeoutInterval()){
                    LogUtil.info(": 任务超时的时间间隔:"+iqConf.getHttpRunnerTaskTimeoutInterval());
                    task.setTaskStatus(HttpRunnerTaskStatus.TIMEOUT.toString());
                    task.setUtime(System.currentTimeMillis());
                    extHttprunnerTaskMapper.updateTaskStatus(task);
                }
            }
        }
    }

    /**
     * 批量设置HttpRunner任务的状态。
     * @param list
     */
    public void changeStatusForErrListTask(List<HttprunnerTask> list,String status){
        for(HttprunnerTask task :list){
            task.setTaskStatus(status);
            task.setUtime(System.currentTimeMillis());
            extHttprunnerTaskMapper.updateTaskStatus(task);
        }
    }


    public int getTaskCountByNodeIdAndStatus(String nodeId,String status){
        return extHttprunnerTaskMapper.getExecutingTaskCountByNodeId(nodeId,status);
    }

    public static void main(String[] args) {
        for(int i =0 ; i< 100 ; i++){
            int index = RandomUtils.nextInt(0,10);
            int index1 = RandomUtils.nextInt(0,0);
            int index2 = RandomUtils.nextInt(0,2);
            System.out.println(index);
            System.out.println(index1);
            System.out.println(index2);
            System.out.println("-----------");
        }
    }
}
