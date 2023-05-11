package io.apirun.api.dto.automation;

import io.apirun.base.domain.ApiTestReport;
import lombok.Data;

@Data
public class ApiTestReportVariable extends ApiTestReport {
    public String executionTime;
    public String executor;
    public String executionEnvironment;
}
