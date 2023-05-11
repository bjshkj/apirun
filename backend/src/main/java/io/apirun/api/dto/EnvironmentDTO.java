package io.apirun.api.dto;

import io.apirun.api.dto.scenario.DatabaseConfig;
import lombok.Data;

@Data
public class EnvironmentDTO {
    private String environmentId;
    private DatabaseConfig databaseConfig;
}
