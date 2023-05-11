package io.apirun.api.dto.automation;

import io.apirun.api.dto.scenario.DatabaseConfig;
import io.apirun.api.dto.scenario.environment.EnvironmentConfig;
import io.apirun.base.domain.ApiTestEnvironmentWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ImportPoolsDTO {
    private String envId;

    private Boolean isCreate;

    private EnvironmentConfig envConfig;

    private ApiTestEnvironmentWithBLOBs testEnvironmentWithBLOBs;

    private Map<String, DatabaseConfig> dataSources;
}
