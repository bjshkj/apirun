package io.apirun.track.dto;

import com.google.gson.annotations.Expose;
import io.apirun.base.domain.TestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class TestPlanCaseDTO extends TestCaseWithBLOBs {
    private String executor;
    private String executorName;
    private transient String status;
    private String results;
    private String planId;
    private String planName;
    private String caseId;
    private String issues;
    private String reportId;
    private String model;
    private String projectName;
    private String actualResult;

    private List<TestCaseTestDTO> list;
}
