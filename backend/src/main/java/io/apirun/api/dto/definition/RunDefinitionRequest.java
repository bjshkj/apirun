package io.apirun.api.dto.definition;

import io.apirun.api.dto.automation.RunModeConfig;
import io.apirun.api.dto.definition.request.MsTestElement;
import io.apirun.api.dto.definition.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class RunDefinitionRequest {

    private String id;

    private String reportId;

    private String requestId;

    private String name;

    private String type;

    private String projectId;

    private String scenarioId;

    private String scenarioName;

    private String environmentId;

    private MsTestElement testElement;

    private String executeType;

    private Response response;

    List<String> bodyFileRequestIds;

    List<String> scenarioFileIds;

    private RunModeConfig config;

    private Map<String, String> environmentMap;
}
