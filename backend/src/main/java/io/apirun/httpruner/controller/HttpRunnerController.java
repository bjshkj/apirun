package io.apirun.httpruner.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.HttprunnerTask;
import io.apirun.base.domain.NodeRegisterInfo;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.config.KafkaProperties;
import io.apirun.httpruner.dto.HttpRunnerTaskStatus;
import io.apirun.httpruner.service.HttpRunnerService;
import io.apirun.httpruner.service.HttpRunnerTasksService;
import io.apirun.security.GuestAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/httpruner")
public class HttpRunnerController {

    @Resource
    KafkaProperties kafkaProperties;

    @Resource
    HttpRunnerTasksService httpRunnerTaskService;

    @Autowired
    HttpRunnerService httpRunerService;

    @PostMapping("/addHttpRunnerTask")
    public void addHttpRunnerTask(@RequestBody HttprunnerTask task){
        task.setId(UUID.randomUUID().toString());
        task.setTaskStatus(HttpRunnerTaskStatus.CREATED.toString());
        task.setCtime(System.currentTimeMillis());
        task.setUtime(System.currentTimeMillis());
        task.setCreateUser(SessionUtils.getUser().getName());
        httpRunerService.addHttpRunnerTask(task);
    }

    @PostMapping("/delHttpRunnerTask")
    public void deleteHttpRunnerTask(@RequestBody HttprunnerTask task){
        httpRunerService.deleteHttpRunnerTask(task);
    }

    @GetMapping("/list/{projectId}/{goPage}/{pageSize}")
    public Pager<List<HttprunnerTask>> getHttRunerTaskList(@PathVariable String projectId ,@PathVariable int goPage, @PathVariable int pageSize){
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page,httpRunerService.getHttpRunnerTasks(projectId));
    }

    @GuestAccess
    @PostMapping("/nodeRegister")
    public HashMap<String,String> nodeRegister(@RequestBody NodeRegisterInfo info){
        HashMap<String,String> hashMap = new HashMap<>();
        if(info == null){
            LogUtil.error("节点信息为空");
            MSException.throwException("节点信息为空");
        }
        if (info.getNodeId() == null ||info.getNodeId().trim().isEmpty()){
            LogUtil.error("当前节点信息中nodeId为空");
            MSException.throwException("当前节点信息中nodeId为空");
        }
        if (info.getIp() == null || info.getIp().trim().isEmpty()){
            LogUtil.error("当前节点信息中IP为空");
            MSException.throwException("当前节点信息中IP为空");
        }
        if (info.getMaxLoad() == null || info.getMaxLoad() <= 0){
            LogUtil.error("当前节点信息中Maxload有误");
            MSException.throwException("当前节点信息中Maxload有误");
        }
//        if (info.getSecretKey() == null || info.getSecretKey().trim().isEmpty()){
//            LogUtil.error("当前节点信息中Secretkey为空");
//            MSException.throwException("当前节点信息中Secretkey为空");
//        }
        if (info.getType() == null || info.getType().trim().isEmpty()){
            LogUtil.error("当前节点信息中type为空");
            MSException.throwException("当前节点信息中type为空");
        }

        int res = httpRunerService.addNodeRegisterInfo(info);
        if (res <= 0){
            LogUtil.error("注册节点信息失败");
            MSException.throwException("注册节点信息失败");
        }
        String bootstrapServers = kafkaProperties.getBootstrapServers();
        String taskTopic = kafkaProperties.getTask().getTopic();
        hashMap.put("bootstrapServers",bootstrapServers);
        hashMap.put("taskTopic",taskTopic);
        return hashMap;
    }


    @GuestAccess
    @PostMapping("/nodeHearbeat")
    public void nodeHearbeat(@RequestBody NodeRegisterInfo info){
        if (info.getNodeId() == null || info.getNodeId().trim().isEmpty()){
            LogUtil.error("当前节点信息中nodeId为空");
            MSException.throwException("当前节点信息中nodeId为空");
        }
        if (info.getNodeStatus() == null || info.getNodeStatus().trim().isEmpty()){
            LogUtil.error("当前节点信息中nodeStatus为空");
            MSException.throwException("当前节点信息中nodeStatus为空");
        }
        info.setUpdateTime(System.currentTimeMillis());
        int res = httpRunerService.updateNodeInfo(info);
        if (res <=0){
            LogUtil.error("节点心跳信息更新失败");
            MSException.throwException("节点心跳信息更新失败");
        }
    }

}
