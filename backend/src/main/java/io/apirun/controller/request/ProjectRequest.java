package io.apirun.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProjectRequest {
    private String workspaceId;
    private String projectId;
    private String userId;
    private String name;
    private String projectType;
    private List<OrderRequest> orders;
    private Map<String, List<String>> filters;
    private Map<String, Object> combine;
}
