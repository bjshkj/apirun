package io.apirun.track.controller;

import io.apirun.commons.constants.OperLogConstants;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.track.dto.TestCaseDTO;
import io.apirun.track.request.issues.IssuesRelevanceRequest;
import io.apirun.track.service.TestCaseIssueService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("test/case/issues")
@RestController
public class TestCaseIssuesController {

    @Resource
    private TestCaseIssueService testCaseIssueService;

    @PostMapping("/list")
    public List<TestCaseDTO> list(@RequestBody IssuesRelevanceRequest request) {
        return testCaseIssueService.list(request);
    }

    @PostMapping("/relate")
    @MsAuditLog(module = "track_test_case", type = OperLogConstants.ASSOCIATE_ISSUE, content = "#msClass.getLogDetails(#request)", msClass = TestCaseIssueService.class)
    public void relate(@RequestBody IssuesRelevanceRequest request) {
        testCaseIssueService.relate(request);
    }
}
