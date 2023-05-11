package io.apirun.track.request.testplan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadCaseReportRequest {
    private String reportId;
    private String testPlanLoadCaseId;
}
