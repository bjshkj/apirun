package io.metersphere.streaming.service;

import io.metersphere.streaming.base.domain.LoadTestReportResult;
import io.metersphere.streaming.base.mapper.LoadTestReportResultMapper;
import io.metersphere.streaming.base.mapper.ext.ExtLoadTestReportResultMapper;
import io.metersphere.streaming.commons.constants.ReportKeys;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestResultSaveService {
    @Resource
    private LoadTestReportResultMapper loadTestReportResultMapper;
    @Resource
    private ExtLoadTestReportResultMapper extLoadTestReportResultMapper;

    public void saveResult(LoadTestReportResult record) {
        int i = extLoadTestReportResultMapper.updateReportValue(record);
        if (i == 0) {
            loadTestReportResultMapper.insertSelective(record);
        }
    }

    public boolean isReportingSet(String reportId) {
        int i = extLoadTestReportResultMapper.updateReportStatus(reportId, ReportKeys.ResultStatus.name(), "Ready", "Reporting");
        return i != 0;
    }

    public void saveReportReadyStatus(String reportId) {
        extLoadTestReportResultMapper.updateReportStatus(reportId, ReportKeys.ResultStatus.name(), "Reporting", "Ready");
    }

    public void saveReportCompletedStatus(String reportId) {
        // 保存最终 为 completed
        extLoadTestReportResultMapper.updateReportStatus(reportId, ReportKeys.ResultStatus.name(), "Reporting", "Completed");
        extLoadTestReportResultMapper.updateReportStatus(reportId, ReportKeys.ResultStatus.name(), "Ready", "Completed");
    }

}
