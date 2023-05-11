package io.apirun.httpruner.schedule;

import io.apirun.base.domain.HttprunnerTask;
import io.apirun.httpruner.service.HttpRunnerService;
import io.apirun.httpruner.service.HttpRunnerTasksService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class HttpRunnerTaskJob {

    @Resource
    HttpRunnerTasksService httpRunnerTasksService;

    @Scheduled(cron = "*/3 * * * * ?")
    public void sendHttpRunnerTask(){
        List<HttprunnerTask> tasks = httpRunnerTasksService.getNeedExecuteTask();
        for ( HttprunnerTask task : tasks ) {
            httpRunnerTasksService.sendHttpRunnerTask(task);
        }
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void checkHttpRunnerTaskFailureJob(){
        httpRunnerTasksService.doHttpRunnerTaskFailure();
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void checkHttpRunnerTaskTimeOut(){
        httpRunnerTasksService.doHttpRunnerTaskTimeOut();
    }
}
