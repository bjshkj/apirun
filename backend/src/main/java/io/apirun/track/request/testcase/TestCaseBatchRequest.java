package io.apirun.track.request.testcase;

import io.apirun.base.domain.TestCaseWithBLOBs;
import io.apirun.controller.request.OrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestCaseBatchRequest extends TestCaseWithBLOBs {
    private List<String> ids;
    private List<OrderRequest> orders;
    private String projectId;

    private QueryTestCaseRequest condition;
}
