package io.apirun.httpruner.controller;

import io.apirun.base.domain.HttprunnerTask;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.httpruner.dto.HttpRunnerTaskResultRequest;
import io.apirun.httpruner.service.HttpRunnerTasksService;
import io.apirun.security.GuestAccess;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/httpRunnerTask")
public class HttpRunnerTasksController {

    @Resource
    HttpRunnerTasksService httpRunnerTaskService;

    @PostMapping("/sendTaskMsg")
    public void sendHttRunnerTask(HttprunnerTask task){
        httpRunnerTaskService.sendHttpRunnerTask(task);
    }

    @GuestAccess
    @PostMapping("/nodeResponse")
    public void httpRunnerTaskResponse(String nodeId, String taskId){
        if (nodeId.trim().isEmpty()){
            LogUtil.error("nodeId为空");
            MSException.throwException("nodeId为空");
        }
        if (taskId.trim().isEmpty()){
            LogUtil.error("taskId为空");
            MSException.throwException("taskId为空");
        }
        httpRunnerTaskService.getResponseFromNode(nodeId,taskId);
    }

    @GuestAccess
    @PostMapping("/httpRunnerTaskResultStatus")
    public void httpRunnerTaskResultStatus(@RequestBody HttpRunnerTaskResultRequest request){
        httpRunnerTaskService.getHttpRunnerTaskResultStatus(request);
    }

}
