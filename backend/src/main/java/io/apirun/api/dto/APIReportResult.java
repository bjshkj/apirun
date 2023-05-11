package io.apirun.api.dto;

import io.apirun.base.domain.ApiTestReport;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class APIReportResult extends ApiTestReport {

    private String testName;

    private String projectName;

    private String userName;

    private List<String> scenarioIds;

    private String content;
}
