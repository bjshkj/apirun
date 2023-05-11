package io.apirun.dto;

import io.apirun.base.domain.TestCaseReviewLoad;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestReviewLoadCaseDTO extends TestCaseReviewLoad {
    private String userName;
    private String caseName;
    private String projectName;
    private String caseStatus;
    private String num;
}
