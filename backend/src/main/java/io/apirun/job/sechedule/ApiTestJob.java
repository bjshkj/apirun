package io.apirun.job.sechedule;

import io.apirun.api.dto.SaveAPITestRequest;
import io.apirun.api.service.APITestService;
import io.apirun.commons.constants.ReportTriggerMode;
import io.apirun.commons.constants.ScheduleGroup;
import io.apirun.commons.utils.CommonBeanFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

public class ApiTestJob extends MsScheduleJob {

    private APITestService apiTestService;
    public ApiTestJob() {
        apiTestService = CommonBeanFactory.getBean(APITestService.class);
    }

    @Override
    void businessExecute(JobExecutionContext context) {
        SaveAPITestRequest request = new SaveAPITestRequest();
        request.setId(resourceId);
        request.setUserId(userId);
        request.setTriggerMode(ReportTriggerMode.SCHEDULE.name());
        apiTestService.run(request);
    }

    public static JobKey getJobKey(String testId) {
        return new JobKey(testId, ScheduleGroup.API_TEST.name());
    }

    public static TriggerKey getTriggerKey(String testId) {
        return new TriggerKey(testId, ScheduleGroup.API_TEST.name());
    }
}

