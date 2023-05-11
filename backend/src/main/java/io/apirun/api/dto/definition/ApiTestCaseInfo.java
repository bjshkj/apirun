package io.apirun.api.dto.definition;

import io.apirun.base.domain.ApiTestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiTestCaseInfo extends ApiTestCaseWithBLOBs {
    private String apiMethod;
}
