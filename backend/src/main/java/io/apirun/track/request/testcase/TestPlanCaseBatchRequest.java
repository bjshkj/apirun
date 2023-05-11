package io.apirun.track.request.testcase;

import io.apirun.base.domain.TestPlanTestCase;
import io.apirun.track.request.testplancase.TestPlanFuncCaseConditions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestPlanCaseBatchRequest extends TestPlanTestCase {
    private List<String> ids;
    private TestPlanFuncCaseConditions condition;
}
