package io.apirun.track.domain;

import io.apirun.api.dto.automation.ApiScenarioDTO;
import io.apirun.api.dto.definition.TestPlanApiCaseDTO;
import io.apirun.track.dto.TestCaseReportMetricDTO;
import io.apirun.track.dto.TestPlanCaseDTO;
import io.apirun.track.dto.TestPlanDTO;
import io.apirun.track.dto.TestPlanLoadCaseDTO;

public abstract class ReportComponent {
    protected String componentId;
    protected TestPlanDTO testPlan;

    public ReportComponent(TestPlanDTO testPlan) {
        this.testPlan = testPlan;
    }

    public abstract void readRecord(TestPlanCaseDTO testCase);

    public abstract void afterBuild(TestCaseReportMetricDTO testCaseReportMetric);

    public void readRecord(TestPlanApiCaseDTO testCase) {
    }

    public void readRecord(ApiScenarioDTO testCase) {
    }

    public void readRecord(TestPlanLoadCaseDTO testCase) {
    }

}
