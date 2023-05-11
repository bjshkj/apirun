package io.apirun.httpruner.service;


import io.apirun.base.domain.HttprunnerTask;
import io.apirun.base.domain.NodeRegisterInfo;
import io.apirun.base.mapper.HttprunnerTaskMapper;
import io.apirun.base.mapper.NodeRegisterInfoMapper;
import io.apirun.base.mapper.ext.ExtHttprunnerTaskMapper;
import io.apirun.base.mapper.ext.ExtNodeRegisterInfoMapper;
import io.apirun.common.Qcm.config.IQConf;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.httpruner.dto.HttpRunnerTaskStatus;
import io.apirun.httpruner.dto.NodeRegisterStatusDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HttpRunnerService {

    @Resource
    HttprunnerTaskMapper httprunnerTaskMapper;

    @Resource
    ExtHttprunnerTaskMapper extHttprunnerTaskMapper;

    @Resource
    ExtNodeRegisterInfoMapper extNodeRegisterInfoMapper;

    @Resource
    NodeRegisterInfoMapper nodeRegisterInfoMapper;

    @Resource
    HttpRunnerTasksService httpRunnerTasksService;

    @Resource
    IQConf iqConf;

    /**
     * 添加httprunnertask的任务
     * @param task
     */
    public void addHttpRunnerTask(HttprunnerTask task){
        int res = httprunnerTaskMapper.insertSelective(task);
    }

    /**
     * 删除HttprunnerTask任务
     * @param task
     * @return
     */
    public int deleteHttpRunnerTask(HttprunnerTask task){
        task.setTaskStatus(HttpRunnerTaskStatus.DELETE.toString());
        task.setUtime(System.currentTimeMillis());
        return extHttprunnerTaskMapper.updateTaskStatus(task);
    }

    /**
     * 通过projectId获取任务
     * @param projectId
     * @return
     */
    public List<HttprunnerTask> getHttpRunnerTasks(String projectId){
        return extHttprunnerTaskMapper.getTaskListByProjectId(projectId);
    }

    /**
     * 通过reportId(taskId)获取任务
     * @param reportId
     * @return
     */
    public HttprunnerTask getHttprunnerTask(String reportId) {
        return httprunnerTaskMapper.selectByPrimaryKey(reportId);
    }

    /**
     * 节点信息注册
     * @param info
     * @return
     */
    public int addNodeRegisterInfo(NodeRegisterInfo info){
        int ans = 0;
        NodeRegisterInfo existNode = nodeRegisterInfoMapper.selectByPrimaryKey(info.getNodeId());
        try{
            if (existNode == null ){
                info.setCreateTime(System.currentTimeMillis());
                info.setUpdateTime(System.currentTimeMillis());
                info.setTotalTasks(0);
                ans = nodeRegisterInfoMapper.insertSelective(info);
            }else {
                info.setUpdateTime(System.currentTimeMillis());
                ans = nodeRegisterInfoMapper.updateByPrimaryKeySelective(info);
            }
        }catch (Exception e){
            LogUtil.error("注册节点时，插入信息失败");
            MSException.throwException("注册节点时，插入信息失败");
        }
        return  ans;
    }

    /**
     * node心跳更新节点信息
     * @param info
     * @return
     */
    public int updateNodeInfo(NodeRegisterInfo info){
        return nodeRegisterInfoMapper.updateByPrimaryKeySelective(info);
    }

    /**
     * 检查节点的信息是否正常
     */
    public void checkNodeHearbeat(){
        List<NodeRegisterInfo> infoList = extNodeRegisterInfoMapper.listNodeByStatus(NodeRegisterStatusDto.RUNNABLE.toString());
        long time = System.currentTimeMillis();
        NodeRegisterInfo node = null;
        for (NodeRegisterInfo info : infoList) {
            node = info;
            if (time - info.getUpdateTime() > iqConf.getNodeHearbeatTimeoutInterval()) {
                LogUtil.info("心跳的时间间隔："+iqConf.getNodeHearbeatTimeoutInterval());
                node.setUpdateTime(time);
                node.setNodeStatus(NodeRegisterStatusDto.UNRUNNABLE.toString());
                nodeRegisterInfoMapper.updateByPrimaryKeySelective(node);
            }
        }
    }

    /**
     * 将UNRUNNABLE的节点置为ERROR
     */
    public void changeNodeStatusToError(){
        List<NodeRegisterInfo> infoList = extNodeRegisterInfoMapper.listNodeByStatus(NodeRegisterStatusDto.UNRUNNABLE.toString());
        long time = System.currentTimeMillis();
        NodeRegisterInfo node = null;
        for (NodeRegisterInfo info : infoList) {
            node = info;
            if (time - info.getUpdateTime() > iqConf.getNodeStatusToErrorInterval()) {
                LogUtil.info("节点为ERROR的时间间隔："+iqConf.getNodeStatusToErrorInterval());
                node.setUpdateTime(time);
                node.setNodeStatus(NodeRegisterStatusDto.ERROR.toString());
                nodeRegisterInfoMapper.updateByPrimaryKeySelective(node);
            }
        }
    }

    /**
     * 将达到最大任务数量的节点状态改为BUSY,不接受任务
     */
    public void updateNodeStatusBusy(){
        List<NodeRegisterInfo> runList = extNodeRegisterInfoMapper.listNodeByStatus(NodeRegisterStatusDto.RUNNABLE.toString());
        NodeRegisterInfo updateInfo = null;
        for(NodeRegisterInfo node : runList){
            updateInfo = node;
            if(node.getMaxLoad()<= httpRunnerTasksService.getTaskCountByNodeIdAndStatus(node.getNodeId(),HttpRunnerTaskStatus.EXECUTING.toString())){
                updateInfo.setNodeStatus(NodeRegisterStatusDto.BUSY.toString());
                updateInfo.setUpdateTime(System.currentTimeMillis());
                extNodeRegisterInfoMapper.udpateNodeStatus(updateInfo);
            }
        }
    }
}
