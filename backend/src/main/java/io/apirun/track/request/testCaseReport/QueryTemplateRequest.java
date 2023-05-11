package io.apirun.track.request.testCaseReport;

import io.apirun.base.domain.TestCaseReportTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryTemplateRequest extends TestCaseReportTemplate {
    Boolean queryDefault;
}
