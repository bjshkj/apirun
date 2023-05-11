package io.apirun.api.dto.definition;

import io.apirun.base.domain.TestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDTO extends TestCaseWithBLOBs {

    private String maintainerName;
    private String apiName;
    private String performName;
}
