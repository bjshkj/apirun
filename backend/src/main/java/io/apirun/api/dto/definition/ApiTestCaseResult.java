package io.apirun.api.dto.definition;

import io.apirun.base.domain.ApiTestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiTestCaseResult extends ApiTestCaseWithBLOBs {
    private String projectName;
    private String createUser;
    private String updateUser;
    private String execResult;
    private String apiMethod;
    private Long execTime;
    private boolean active = false;
    private boolean responseActive = false;
}
