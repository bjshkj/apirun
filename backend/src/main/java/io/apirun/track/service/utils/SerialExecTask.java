/**
 *
 */
package io.apirun.track.service.utils;

import io.apirun.api.dto.automation.RunModeConfig;
import io.apirun.base.domain.LoadTestReportWithBLOBs;
import io.apirun.base.domain.TestPlanLoadCase;
import io.apirun.base.mapper.LoadTestReportMapper;
import io.apirun.base.mapper.TestPlanLoadCaseMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.performance.request.RunTestPlanRequest;
import io.apirun.performance.service.PerformanceTestService;

import java.util.concurrent.Callable;

public class SerialExecTask<T> implements Callable<T> {
    private RunTestPlanRequest request;
    private RunModeConfig config;
    private PerformanceTestService performanceTestService;
    private TestPlanLoadCaseMapper testPlanLoadCaseMapper;
    private LoadTestReportMapper loadTestReportMapper;

    public SerialExecTask(PerformanceTestService performanceTestService, TestPlanLoadCaseMapper testPlanLoadCaseMapper,LoadTestReportMapper loadTestReportMapper, RunTestPlanRequest request, RunModeConfig config) {
        this.performanceTestService = performanceTestService;
        this.testPlanLoadCaseMapper = testPlanLoadCaseMapper;
        this.loadTestReportMapper = loadTestReportMapper;
        this.request = request;
        this.config = config;
    }

    @Override
    public T call() {
        try {
            // 串行，开启轮询等待
            String reportId = performanceTestService.run(request);
            TestPlanLoadCase testPlanLoadCase = new TestPlanLoadCase();
            testPlanLoadCase.setId(request.getTestPlanLoadId());
            testPlanLoadCase.setLoadReportId(reportId);
            testPlanLoadCaseMapper.updateByPrimaryKeySelective(testPlanLoadCase);
            LoadTestReportWithBLOBs report = null;
            // 轮询查看报告状态，最多200次，防止死循环
            int index = 1;
            while (index < 200) {
                Thread.sleep(3000);
                index++;
                report = loadTestReportMapper.selectByPrimaryKey(reportId);
                if (report != null && (report.getStatus().equals("Completed") || report.getStatus().equals("Error") || report.getStatus().equals("Saved"))) {
                    break;
                }
            }
            return (T) report;

        } catch (Exception ex) {
            LogUtil.error(ex.getMessage());
            MSException.throwException(ex.getMessage());
            return null;
        }
    }
}
