package io.apirun.api.dto;

import io.apirun.base.domain.ApiTestCaseWithBLOBs;
import io.apirun.controller.request.OrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiCaseBatchRequest extends ApiTestCaseWithBLOBs {
    private List<String> ids;
    private List<OrderRequest> orders;
    private String projectId;
}
