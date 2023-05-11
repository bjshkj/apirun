package io.apirun.httpruner.dto;

import io.apirun.base.domain.TestCaseResult;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TaskSummaryResult {

    private String taskId;
    private Date startTime;
    private Date finishTime;
    private StepStatInfo stepStatInfo;
    private TestCaseStatInfo testCaseStatInfo;
    private double duration = 0;
    private String platform;
    private String testlogId;
    private List<TestCaseResult> testCaseResults;
}
