package io.apirun.api.dto.automation;

import io.apirun.controller.request.BaseQueryRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiScenarioRequest extends BaseQueryRequest {
    private String id;
    private String excludeId;
    private String moduleId;
    private String name;
    private String userId;
    private String planId;
    private boolean recent = false;
    private boolean isSelectThisWeedData;
    private long createTime = 0;
    private String executeStatus;
    private boolean notInTestPlan;
    private String reviewId;
}
