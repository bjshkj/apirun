package io.apirun.track.request.testplancase;

import io.apirun.base.domain.TestPlanTestCase;
import io.apirun.controller.request.OrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryTestPlanCaseRequest extends TestPlanTestCase {

    private List<String> nodeIds;

    private List<String> nodePaths;

    private List<OrderRequest> orders;

    private Map<String, List<String>> filters;

    private List<String> planIds;

    private List<String> projectIds;

    private String workspaceId;

    private String name;

    private String status;

    private String node;

    private String method;

    private String nodeId;

    private Map<String, Object> combine;
}
