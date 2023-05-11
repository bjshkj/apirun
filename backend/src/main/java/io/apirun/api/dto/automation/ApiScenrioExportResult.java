package io.apirun.api.dto.automation;

import io.apirun.base.domain.ApiScenarioWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiScenrioExportResult {
    private String projectId;
    private String version;
    private List<ApiScenarioWithBLOBs> data;
}
