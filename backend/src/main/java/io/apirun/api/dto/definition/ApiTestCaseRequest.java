package io.apirun.api.dto.definition;

import io.apirun.controller.request.BaseQueryRequest;
import io.apirun.controller.request.OrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ApiTestCaseRequest extends BaseQueryRequest {
    private String id;
    private List<String> ids;
    private String planId;
    private String projectId;
    private String priority;
    private String name;
    private String environmentId;
    private String workspaceId;
    private String apiDefinitionId;
    private String status;
    private String protocol;
    private List<String> moduleIds;
    private List<OrderRequest> orders;
    private Map<String, List<String>> filters;
    private Map<String, Object> combine;
    private boolean isSelectThisWeedData;
    private long createTime = 0;
    private String reviewId;
}
