package io.apirun.api.dto.definition;

import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.controller.request.OrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiBatchRequest extends ApiDefinitionWithBLOBs {
    private List<String> ids;
    private String name;
    private List<OrderRequest> orders;
    private String projectId;
    private String moduleId;
    private String protocol;

    private ApiDefinitionRequest condition;
}
