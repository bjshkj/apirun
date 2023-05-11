package io.apirun.httpruner.schedule;

import io.apirun.httpruner.service.HttpRunnerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NodeScheduleTask {

    @Resource
    HttpRunnerService httpRunerService;

    @Scheduled(cron = "*/1 * * * * ?")
    public void checkNodeStatusJob(){
        httpRunerService.checkNodeHearbeat();
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void changeNodeStatusToErrorJob(){
        httpRunerService.changeNodeStatusToError();
    }

    @Scheduled(cron = "*/1 * * * * ?")
    public void updateNodeStatusBusyJob(){
        httpRunerService.updateNodeStatusBusy();
    }

}
