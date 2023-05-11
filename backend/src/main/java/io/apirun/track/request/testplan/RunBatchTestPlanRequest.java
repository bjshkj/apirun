package io.apirun.track.request.testplan;

import io.apirun.api.dto.automation.RunModeConfig;
import io.apirun.performance.request.RunTestPlanRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RunBatchTestPlanRequest  {
    private List<RunTestPlanRequest> requests;
    private RunModeConfig config;
    private String userId;
}
