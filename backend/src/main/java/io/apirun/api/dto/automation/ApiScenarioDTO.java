package io.apirun.api.dto.automation;

import io.apirun.base.domain.ApiScenarioWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiScenarioDTO extends ApiScenarioWithBLOBs {

    private String projectName;
    private String userName;
    private List<String> tagNames;

    /**
     * 场景跨项目ID
     */
    private List<String> projectIds;

    private String caseId;
}
