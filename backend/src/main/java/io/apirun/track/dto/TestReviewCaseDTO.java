package io.apirun.track.dto;

import io.apirun.base.domain.TestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestReviewCaseDTO extends TestCaseWithBLOBs {
    private String reviewer;
    private String reviewerName;
    private String reviewStatus;
    private String results;
    private String reviewId;
    private String caseId;
    private String issues;
    private String model;
    private String projectName;
    private List<TestCaseTestDTO> list;
}
