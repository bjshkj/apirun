package io.metersphere.streaming.report.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.streaming.base.domain.LoadTestReportResult;
import io.metersphere.streaming.commons.utils.CommonBeanFactory;
import io.metersphere.streaming.commons.utils.LogUtil;
import io.metersphere.streaming.report.Report;
import io.metersphere.streaming.service.TestResultSaveService;
import org.apache.jmeter.report.processor.SampleContext;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractReport implements Report {

    protected String reportId;
    protected Map<String, SampleContext> sampleContextMap;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TestResultSaveService testResultSaveService;
    protected DecimalFormat format2 = new DecimalFormat("0.00");

    public void init(String reportId, Map<String, SampleContext> sampleContextMap) {
        this.reportId = reportId;
        this.sampleContextMap = sampleContextMap;
    }

    public AbstractReport() {
        this.testResultSaveService = CommonBeanFactory.getBean(TestResultSaveService.class);
    }

    public void saveResult(String reportId, Object content) {
        LoadTestReportResult record = new LoadTestReportResult();
        record.setId(UUID.randomUUID().toString());
        record.setReportId(reportId);
        record.setReportKey(getReportKey());
        try {
            record.setReportValue(objectMapper.writeValueAsString(content));
        } catch (JsonProcessingException e) {
            LogUtil.error(e);
        }
        testResultSaveService.saveResult(record);
        LogUtil.info("Report generate success: {}, reportId: {}", getReportKey(), reportId);
    }
}
