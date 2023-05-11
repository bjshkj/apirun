package io.apirun.base.domain;

import io.apirun.httpruner.dto.StepStatInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestCaseResult {

    private String taskId;
    private int index;
    private String name;
    private Date startTime;
    private Boolean success = false;
    private StepStatInfo stepStatInfo;
    private double duration = 0;
    private String in;
    private String out;
    private List<TestStepResult> testStepResults;
}
