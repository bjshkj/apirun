package io.apirun.track.service;

import io.apirun.base.domain.LoadTestReport;
import io.apirun.base.mapper.ext.ExtTestPlanLoadCaseMapper;
import io.apirun.commons.constants.PerformanceTestStatus;
import io.apirun.commons.constants.ReportTriggerMode;
import io.apirun.commons.consumer.LoadTestFinishEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Transactional(rollbackFor = Exception.class)
public class LoadReportStatusEvent implements LoadTestFinishEvent {

    @Resource
    private ExtTestPlanLoadCaseMapper extTestPlanLoadCaseMapper;

    private void updateLoadCaseStatus(LoadTestReport loadTestReport) {
        String reportId = loadTestReport.getId();
        String status = loadTestReport.getStatus();
        if (StringUtils.isNotBlank(reportId)) {
            String result = "";
            if (StringUtils.equals(PerformanceTestStatus.Error.name(), status)) {
                result = "error";
            }
            if (StringUtils.equals(PerformanceTestStatus.Completed.name(), status)) {
                result = "success";
            }
            extTestPlanLoadCaseMapper.updateCaseStatus(reportId, result);
        }
    }

    @Override
    public void execute(LoadTestReport loadTestReport) {
        if (StringUtils.equals(ReportTriggerMode.CASE.name(), loadTestReport.getTriggerMode())
                || StringUtils.equals(ReportTriggerMode.TEST_PLAN_SCHEDULE.name(), loadTestReport.getTriggerMode())) {
            if (StringUtils.equalsAny(loadTestReport.getStatus(),
                    PerformanceTestStatus.Completed.name(), PerformanceTestStatus.Error.name())) {
                updateLoadCaseStatus(loadTestReport);
            }
        }
    }
}
