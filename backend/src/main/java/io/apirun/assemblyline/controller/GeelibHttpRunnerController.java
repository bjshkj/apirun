package io.apirun.assemblyline.controller;

import io.apirun.assemblyline.dto.HttpRunnerDto;
import io.apirun.base.domain.HttprunnerTask;
import io.apirun.assemblyline.dto.GeelibRequest;
import io.apirun.assemblyline.service.GeelibGttpRunnerService;
import io.apirun.base.domain.User;
import io.apirun.base.mapper.UserMapper;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.httpruner.dto.HttpRunnerTaskStatus;
import io.apirun.httpruner.dto.TestCaseStatInfo;
import io.apirun.httpruner.dto.TriggerModelStatus;
import io.apirun.httpruner.service.HttpRunnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/geelib")
public class GeelibHttpRunnerController {

    @Resource
    private GeelibGttpRunnerService geelibGttpRunnerService;
    @Resource
    private UserMapper userMapper;

    @Autowired
    HttpRunnerService httpRunerService;

    /**
     * 对接极库云流水线创建httprunner任务
     * @param geelibRequest
     * @return
     */
    @PostMapping("/addHttpRunnerTask")
    public HttpRunnerDto addHttpRunnerTaskBySTREAM(@RequestBody GeelibRequest geelibRequest){
        HttpRunnerDto dto = geelibGttpRunnerService.CalibrationParameters(geelibRequest);
        if (dto.isStatus()){
            HttprunnerTask task = new HttprunnerTask();
            task.setProjectId(geelibRequest.getId());
            task.setDotEnvPath(geelibRequest.getEnvPath());
            task.setHostsPath(geelibRequest.getHostsPath());
            task.setExecCasePaths(geelibRequest.getExecPath());
            task.setTaskName(geelibRequest.getName());
            task.setGitVersion(geelibRequest.getCommit());
            task.setId(UUID.randomUUID().toString());
            task.setTaskStatus(HttpRunnerTaskStatus.CREATED.toString());
            task.setCtime(System.currentTimeMillis());
            task.setUtime(System.currentTimeMillis());
            task.setDebugMode("INFO");
            task.setTriggerModel(TriggerModelStatus.STREAM.toString());
            if (StringUtils.isNotBlank(geelibRequest.getEmail())){
                User user = userMapper.selectByEmail(geelibRequest.getEmail());
                if (user != null){
                    task.setCreateUser(user.getName());
                }else {
                    task.setCreateUser(geelibRequest.getEmail());
                }
            }
            httpRunerService.addHttpRunnerTask(task);
            dto.setTaskId(task.getId());
        }
        return dto;
    }

    /**
     * 对接极库云流水线查询httprunner任务执行结果
     * @param taskId
     * @return
     */
    @GetMapping("/getTaskStatus/{taskId}")
    public TestCaseStatInfo getHttprunnerTaskStatus(@PathVariable String taskId){
        return geelibGttpRunnerService.getTaskStatus(taskId);
    }
}
