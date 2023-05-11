package io.apirun.track.domain;

import io.apirun.api.dto.automation.ApiScenarioDTO;
import io.apirun.api.dto.automation.ScenarioStatus;
import io.apirun.api.dto.definition.TestPlanApiCaseDTO;
import io.apirun.commons.constants.TestPlanTestCaseStatus;
import io.apirun.track.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportFailureAdvanceResultComponent extends ReportComponent {
    private List<TestPlanCaseDTO> functionalTestCases = new ArrayList<>();
    private List<TestPlanApiCaseDTO> apiTestCases = new ArrayList<>();
    private List<ApiScenarioDTO> scenarioTestCases = new ArrayList<>();
    private List<TestPlanLoadCaseDTO> loadTestCases = new ArrayList<>();

    public ReportFailureAdvanceResultComponent(TestPlanDTO testPlan) {
        super(testPlan);
        componentId = "4";
    }

    @Override
    public void readRecord(TestPlanCaseDTO testCase) {
        if (StringUtils.equals(testCase.getStatus(), TestPlanTestCaseStatus.Failure.name()) ||
            StringUtils.equals(testCase.getStatus(), TestPlanTestCaseStatus.Blocking.name())) {
            this.functionalTestCases.add(testCase);
        }
    }

    @Override
    public void readRecord(TestPlanApiCaseDTO testCase) {
        if (StringUtils.equals(testCase.getExecResult(), "error")) {
            this.apiTestCases.add(testCase);
        }
    }

    @Override
    public void readRecord(ApiScenarioDTO testCase) {
        if (StringUtils.equals(testCase.getLastResult(), ScenarioStatus.Fail.name())) {
            this.scenarioTestCases.add(testCase);
        }
    }

    @Override
    public void readRecord(TestPlanLoadCaseDTO testCase) {
        if (StringUtils.equals(testCase.getCaseStatus(), "error")) {
            this.loadTestCases.add(testCase);
        }
    }

    @Override
    public void afterBuild(TestCaseReportMetricDTO testCaseReportMetric) {
        FailureTestCasesAdvanceDTO failureTestCasesAdvanceDTO = new FailureTestCasesAdvanceDTO();
        failureTestCasesAdvanceDTO.setFunctionalTestCases(functionalTestCases);
        failureTestCasesAdvanceDTO.setApiTestCases(apiTestCases);
        failureTestCasesAdvanceDTO.setScenarioTestCases(scenarioTestCases);
        failureTestCasesAdvanceDTO.setLoadTestCases(loadTestCases);
        testCaseReportMetric.setFailureTestCases(failureTestCasesAdvanceDTO);
    }
}
