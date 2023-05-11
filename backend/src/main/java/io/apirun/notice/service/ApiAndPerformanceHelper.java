package io.apirun.notice.service;

import io.apirun.api.dto.APIReportResult;
import io.apirun.base.domain.ApiTestReportDetail;
import io.apirun.base.domain.LoadTestReportWithBLOBs;
import io.apirun.base.domain.Schedule;
import io.apirun.base.mapper.ApiTestReportDetailMapper;
import io.apirun.base.mapper.LoadTestReportMapper;
import io.apirun.base.mapper.ext.ExtApiTestReportMapper;
import io.apirun.base.mapper.ext.ExtLoadTestMapper;
import io.apirun.commons.constants.ScheduleGroup;
import io.apirun.dto.LoadTestDTO;
import io.apirun.performance.request.QueryTestPlanRequest;
import io.apirun.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class ApiAndPerformanceHelper {
    @Resource
    private ExtLoadTestMapper extLoadTestMapper;
    @Resource
    private ExtApiTestReportMapper extApiTestReportMapper;
    @Resource
    private ApiTestReportDetailMapper apiTestReportDetailMapper;
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private LoadTestReportMapper loadTestReportMapper;

    public APIReportResult getApi(String reportId) {
        APIReportResult result = extApiTestReportMapper.get(reportId);
        ApiTestReportDetail detail = apiTestReportDetailMapper.selectByPrimaryKey(reportId);
        if (detail != null) {
            result.setContent(new String(detail.getContent(), StandardCharsets.UTF_8));
        }
        return result;
    }

    public LoadTestDTO getPerformance(String testId) {
        QueryTestPlanRequest request = new QueryTestPlanRequest();
        request.setId(testId);
        List<LoadTestDTO> testDTOS = extLoadTestMapper.list(request);
        if (!CollectionUtils.isEmpty(testDTOS)) {
            LoadTestDTO loadTestDTO = testDTOS.get(0);
            Schedule schedule = scheduleService.getScheduleByResource(loadTestDTO.getId(), ScheduleGroup.PERFORMANCE_TEST.name());
            loadTestDTO.setSchedule(schedule);
            return loadTestDTO;
        }
        return null;
    }

    public LoadTestReportWithBLOBs getLoadTestReport(String id) {
        return loadTestReportMapper.selectByPrimaryKey(id);
    }
}

