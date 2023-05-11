package io.apirun.track.domain;

import io.apirun.commons.constants.TestPlanTestCaseStatus;
import io.apirun.track.dto.TestCaseReportMetricDTO;
import io.apirun.track.dto.TestPlanCaseDTO;
import io.apirun.track.dto.TestPlanDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportFailureResultComponent extends ReportComponent {
    private List<TestPlanCaseDTO> failureTestCases = new ArrayList<>();

    public ReportFailureResultComponent(TestPlanDTO testPlan) {
        super(testPlan);
        componentId = "4";
    }

    @Override
    public void readRecord(TestPlanCaseDTO testCase) {
        if (StringUtils.equals(testCase.getStatus(), TestPlanTestCaseStatus.Failure.name())) {
            this.failureTestCases.add(testCase);
        }
    }

    @Override
    public void afterBuild(TestCaseReportMetricDTO testCaseReportMetric) {
//        testCaseReportMetric.setFailureTestCases(failureTestCases);
    }
}
