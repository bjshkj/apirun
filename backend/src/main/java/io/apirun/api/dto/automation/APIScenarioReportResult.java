package io.apirun.api.dto.automation;

import io.apirun.base.domain.ApiScenarioReport;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class APIScenarioReportResult extends ApiScenarioReport {

    private String testName;

    private String projectName;

    private String testId;

    private String userName;

    private List<String> scenarioIds;

    private String content;
}
