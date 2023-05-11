package io.apirun.api.dto.definition;

import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.base.domain.ApiTestCaseWithBLOBs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MsApiExportResult extends ApiExportResult {
    private String projectName;
    private String protocol;
    private String projectId;
    private String version;
    private List<ApiDefinitionWithBLOBs> data;
    private List<ApiTestCaseWithBLOBs> cases;
}
