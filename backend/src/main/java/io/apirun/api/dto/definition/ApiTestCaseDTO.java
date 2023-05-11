package io.apirun.api.dto.definition;

import io.apirun.base.domain.ApiTestCase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiTestCaseDTO extends ApiTestCase {
    private String moduleId;
    private String path;
    private String protocol;
    private String casePath;
    private String updateUser;
    private String createUser;
}
