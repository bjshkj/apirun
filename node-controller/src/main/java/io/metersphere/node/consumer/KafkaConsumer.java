package io.metersphere.node.consumer;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.api.jmeter.utils.MSException;
import io.metersphere.node.constants.TaskStatus;
import io.metersphere.node.dto.TestTask;
import io.metersphere.node.http.QHttpResponse;
import io.metersphere.node.service.HttpRunnerOperateService;
import io.metersphere.node.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class KafkaConsumer {

    private static final String CONSUME_ID = "ms-httpRunner-consume";
    private static final String PATH = "/home/docker/qhttprunner/";
    private static final String DOWN_URL = "/gitLab/getFileArchive";
    private static final String TASK_RECEIVE_URL = "/httpRunnerTask/nodeResponse";
    private static final String TASK_END_URL = "/httpRunnerTask/httpRunnerTaskResultStatus";

    @Value("${backend.address}")
    private String address;

    @Resource
    private HttpRunnerOperateService httpRunnerOperateService;

    @KafkaListener(id = CONSUME_ID, topics = "${kafka.task.topic}")
    public void consume(ConsumerRecord<?, String> record){
        LogUtil.info("接收到任务消息");
        //判断消息中的nodeID是否是自己的id，不是丢弃
        TestTask testTask = formatResult(record.value());
        String nodeId = testTask.getNodeId();
        if (nodeId.equals(HostAddress.getLocalIP().replace(".",""))){
            LogUtil.info("接收到任务消息，属于自己的id:" + testTask);
            //调用接口，上报任务已经接收，返回是否需要执行
            String taskId = testTask.getTaskId();
            String flag = taskReceive(nodeId,taskId);
            if (flag.equals("true")){
                //根据projectId和commitType发起http请求，下载对应的代码zip包
                downloadZip(testTask);
                //获取宿主机目录
                String host_Directory = FileUtil.traverseFolder(PATH + taskId);
                //解析host文件
                List<String> host = FileUtil.analysisFile(host_Directory + "/" + testTask.getHostsPath());
                String[] hosts = host.toArray(new String[0]);
                LogUtil.info("解析文件完成:" + host);
                //根据任务消息数据，创建docker容器 运行
                httpRunnerOperateService.startContainer(testTask, host_Directory, hosts);
            }else {
                LogUtil.info("任务不需要执行:" + testTask);
            }
        }else {
            LogUtil.info("不是此节点:" + nodeId + "丢弃任务");
        }
    }

    /**
     * 批量将json字符串转为对象
     * @param result
     * @return
     */
    private TestTask formatResult(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            // 多态JSON普通转换会丢失内容，需要通过 ObjectMapper 获取
            if (StringUtils.isNotEmpty(result)) {
                TestTask testTask = mapper.readValue(result, new TypeReference<TestTask>() {
                });
                return testTask;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        }
        return null;
    }

    /**
     * 下载代码zip包
     * @param testTask
     */
    private void downloadZip(TestTask testTask){
        String taskId = testTask.getTaskId();
        //项目id
        String id = testTask.getProjectId();
        //commit版本
        String sha = testTask.getGitVersion();
        //下载路径
        String localPath = PATH + taskId + ".zip";
        String params = "id=" + id + "&sha=" +sha;
        byte[] fileArchive = HttpUtil.doGetRequestForFile(String.format("%s"+DOWN_URL,address),params);
        if (fileArchive != null){
            try {
                File zipFile = new File(localPath);
                if (!zipFile.exists()){
                    zipFile.getParentFile().mkdirs();
                    zipFile.createNewFile();
                }
                OutputStream outputStream = new FileOutputStream(zipFile);
                outputStream.write(fileArchive);
                outputStream.flush();
                outputStream.close();
                FileUtil.unZipFiles(zipFile,PATH);
                LogUtil.info("代码拉取已完成");
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("代码拉取失败：" + e.getMessage());
                //代码拉取失败，上报任务状态
                taskEndState(taskId, String.valueOf(TaskStatus.FAILURE));
            }
        }else {
            MSException.throwException("代码拉取出现异常");
        }
    }

    /**
     * 上报任务已经接收
     * @param nodeId
     * @param taskId
     * @return
     */
    private String taskReceive(String nodeId,String taskId){
        String params = "nodeId=" + nodeId + "&taskId=" + taskId;
        QHttpResponse qHttpResponse = HttpUtil.post(String.format("%s"+TASK_RECEIVE_URL,address),params,null);
        String flag = "true";
        if (qHttpResponse != null){
            flag = String.valueOf(qHttpResponse.getJsonNode().get("success"));
            String msg = String.valueOf(qHttpResponse.getJsonNode().get("message"));
            LogUtil.info(msg);
        }else {
            MSException.throwException("任务接收出现异常");
        }
        return flag;
    }

    /**
     * 上报任务结束状态
     * @param taskId    任务id
     * @param taskStatus 任务的状态 SUCCESS/执行成功 FAILURE/执行失败 ERROR/7-平台失败
     * @return
     */
    private void taskEndState(String taskId, String taskStatus){
        JSONObject object = new JSONObject();
        object.put("id", taskId);
        object.put("taskStatus", taskStatus);
        String params = object.toString();
        QHttpResponse qHttpResponse = HttpUtil.postJson(String.format("%s"+TASK_END_URL,address),params,null);
        if(qHttpResponse != null){
            String msg = String.valueOf(qHttpResponse.getJsonNode().get("message"));
            LogUtil.info("通知服务器任务结束，服务器返回消息：" + msg);
        }else {
            MSException.throwException("通知服务器任务接收接口出现异常");
        }
    }
}
