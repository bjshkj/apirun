package io.apirun.track.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.api.dto.automation.*;
import io.apirun.commons.constants.ApiRunMode;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.track.dto.RelevanceScenarioRequest;
import io.apirun.track.request.testcase.TestPlanScenarioCaseBatchRequest;
import io.apirun.track.service.TestPlanScenarioCaseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/test/plan/scenario/case")
@RestController
public class TestPlanScenarioCaseController {

    @Resource
    TestPlanScenarioCaseService testPlanScenarioCaseService;

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ApiScenarioDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody TestPlanScenarioRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, testPlanScenarioCaseService.list(request));
    }

    @PostMapping("/selectAllTableRows")
    public List<ApiScenarioDTO> selectAllTableRows(@RequestBody TestPlanScenarioCaseBatchRequest request) {
        return testPlanScenarioCaseService.selectAllTableRows(request);
    }

    @PostMapping("/relevance/list/{goPage}/{pageSize}")
    public Pager<List<ApiScenarioDTO>> relevanceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ApiScenarioRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return PageUtils.setPageInfo(page, testPlanScenarioCaseService.relevanceList(request));
    }

    @GetMapping("/delete/{id}")
    @MsAuditLog(module = "track_test_case_review", type = OperLogConstants.UN_ASSOCIATE_CASE, beforeEvent = "#msClass.getLogDetails(#id)", msClass = TestPlanScenarioCaseService.class)
    public int deleteTestCase(@PathVariable String id) {
        return testPlanScenarioCaseService.delete(id);
    }

    @PostMapping("/batch/delete")
    @MsAuditLog(module = "track_test_plan", type = OperLogConstants.UN_ASSOCIATE_CASE, beforeEvent = "#msClass.getLogDetails(#request.ids)", msClass = TestPlanScenarioCaseService.class)
    public void deleteApiCaseBath(@RequestBody TestPlanScenarioCaseBatchRequest request) {
        testPlanScenarioCaseService.deleteApiCaseBath(request);
    }

    @PostMapping(value = "/run")
    @MsAuditLog(module = "track_test_plan", type = OperLogConstants.EXECUTE, content = "#msClass.getLogDetails(#request.planCaseIds)", msClass = TestPlanScenarioCaseService.class)
    public String run(@RequestBody RunTestPlanScenarioRequest request) {
        request.setExecuteType(ExecuteType.Completed.name());
        return testPlanScenarioCaseService.run(request);
    }

    @PostMapping(value = "/jenkins/run")
    @MsAuditLog(module = "track_test_plan", type = OperLogConstants.EXECUTE, content = "#msClass.getLogDetails(#request.ids)", msClass = TestPlanScenarioCaseService.class)
    public String runByRun(@RequestBody RunTestPlanScenarioRequest request) {
        request.setExecuteType(ExecuteType.Saved.name());
        request.setTriggerMode(ApiRunMode.API.name());
        request.setRunMode(ApiRunMode.SCENARIO.name());
        return testPlanScenarioCaseService.run(request);
    }

    @PostMapping("/batch/update/env")
    @MsAuditLog(module = "track_test_plan", type = OperLogConstants.BATCH_UPDATE, beforeEvent = "#msClass.batchLogDetails(#request.ids)", content = "#msClass.getLogDetails(#request.ids)", msClass = TestPlanScenarioCaseService.class)
    public void batchUpdateEnv(@RequestBody RelevanceScenarioRequest request) {
        testPlanScenarioCaseService.batchUpdateEnv(request);
    }
}
