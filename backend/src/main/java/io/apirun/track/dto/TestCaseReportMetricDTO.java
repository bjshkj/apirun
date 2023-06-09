package io.apirun.track.dto;

import io.apirun.base.domain.IssuesDao;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestCaseReportMetricDTO {

//    private List<TestCaseReportStatusResultDTO> executeResult;
    private TestCaseReportAdvanceStatusResultDTO executeResult;
    private List<TestCaseReportModuleResultDTO> moduleExecuteResult;
    private FailureTestCasesAdvanceDTO failureTestCases;
//    private List<TestPlanCaseDTO> failureTestCases;
    private List<IssuesDao> Issues;
    private List<String> executors;
    private String principal;
    private Long startTime;
    private Long endTime;
    private String projectName;

}
