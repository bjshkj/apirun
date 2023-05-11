package io.apirun.track.service;

import io.apirun.base.domain.LoadTestReport;
import io.apirun.commons.constants.ReportTriggerMode;
import io.apirun.commons.consumer.LoadTestFinishEvent;
import io.apirun.commons.utils.LogUtil;
import io.apirun.track.dto.TestPlanLoadCaseEventDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author song.tianyang
 * @Date 2021/1/13 2:53 下午
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPlanLoadCaseEvent implements LoadTestFinishEvent {
    @Resource
    TestPlanReportService testPlanReportService;

    @Override
    public void execute(LoadTestReport loadTestReport) {
        LogUtil.info("PerformanceNoticeEvent OVER:" + loadTestReport.getTriggerMode()+";"+loadTestReport.getStatus());
        if (StringUtils.equals(ReportTriggerMode.TEST_PLAN_SCHEDULE.name(), loadTestReport.getTriggerMode()) ) {
            TestPlanLoadCaseEventDTO eventDTO = new TestPlanLoadCaseEventDTO();
            eventDTO.setReportId(loadTestReport.getId());
            eventDTO.setTriggerMode(ReportTriggerMode.SCHEDULE.name());
            eventDTO.setStatus(loadTestReport.getStatus());
            testPlanReportService.updatePerformanceTestStatus(eventDTO);
        }
    }
}
