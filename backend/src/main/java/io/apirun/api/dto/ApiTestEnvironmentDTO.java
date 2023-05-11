package io.apirun.api.dto;

import io.apirun.base.domain.ApiTestEnvironmentWithBLOBs;
import lombok.Data;

import java.util.List;

@Data
public class ApiTestEnvironmentDTO extends ApiTestEnvironmentWithBLOBs {
    private List<String> uploadIds;
}
