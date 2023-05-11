package io.apirun.track.request.testplan;

import io.apirun.base.domain.TestPlanLoadCase;
import io.apirun.controller.request.OrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LoadCaseRequest extends TestPlanLoadCase {
    private List<String> ids;
    private String projectId;
    private List<String> caseIds;
    private String name;
    private String status;
    private Map<String, List<String>> filters;
    private List<OrderRequest> orders;

}
