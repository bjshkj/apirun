package io.apirun.track.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.Project;
import io.apirun.base.domain.TestCaseReview;
import io.apirun.base.domain.User;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.constants.PermissionConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.CheckPermissionService;
import io.apirun.track.dto.TestCaseReviewDTO;
import io.apirun.track.dto.TestReviewDTOWithMetric;
import io.apirun.track.request.testreview.QueryCaseReviewRequest;
import io.apirun.track.request.testreview.ReviewRelevanceRequest;
import io.apirun.track.request.testreview.SaveTestCaseReviewRequest;
import io.apirun.track.request.testreview.TestReviewRelevanceRequest;
import io.apirun.track.service.TestCaseReviewService;
import io.apirun.track.service.TestReviewProjectService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RequestMapping("/test/case/review")
@RestController

public class TestCaseReviewController {

    @Resource
    TestCaseReviewService testCaseReviewService;
    @Resource
    TestReviewProjectService testReviewProjectService;
    @Resource
    CheckPermissionService checkPermissionService;

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<TestCaseReviewDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryCaseReviewRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, testCaseReviewService.listCaseReview(request));
    }

    @PostMapping("/save")
    @RequiresPermissions(PermissionConstants.PROJECT_TRACK_REVIEW_READ_CREATE)
    @MsAuditLog(module = "track_test_case_review", type = OperLogConstants.CREATE, title = "#reviewRequest.name", content = "#msClass.getLogDetails(#reviewRequest.id)", msClass = TestCaseReviewService.class)
    public String saveCaseReview(@RequestBody SaveTestCaseReviewRequest reviewRequest) {
        reviewRequest.setId(UUID.randomUUID().toString());
        return testCaseReviewService.saveTestCaseReview(reviewRequest);
    }

    @PostMapping("/project")
    public List<Project> getProjectByReviewId(@RequestBody TestCaseReview request) {
        return testCaseReviewService.getProjectByReviewId(request);
    }

    @PostMapping("/reviewer")
    public List<User> getUserByReviewId(@RequestBody TestCaseReview request) {
        return testCaseReviewService.getUserByReviewId(request);
    }

    @GetMapping("/recent/{count}")
    public List<TestCaseReviewDTO> recentTestPlans(@PathVariable int count) {
        String currentWorkspaceId = SessionUtils.getCurrentWorkspaceId();
        PageHelper.startPage(1, count, true);
        return testCaseReviewService.recent(currentWorkspaceId);
    }

    @PostMapping("/edit")
    @RequiresPermissions(PermissionConstants.PROJECT_TRACK_REVIEW_READ_EDIT)
    @MsAuditLog(module = "track_test_case_review", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#testCaseReview.id)", title = "#testCaseReview.name", content = "#msClass.getLogDetails(#testCaseReview.id)", msClass = TestCaseReviewService.class)
    public String editCaseReview(@RequestBody SaveTestCaseReviewRequest testCaseReview) {
        return testCaseReviewService.editCaseReview(testCaseReview);
    }

    @GetMapping("/delete/{reviewId}")
    @RequiresPermissions(PermissionConstants.PROJECT_TRACK_REVIEW_READ_DELETE)
    @MsAuditLog(module = "track_test_case_review", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#reviewId)", msClass = TestCaseReviewService.class)
    public void deleteCaseReview(@PathVariable String reviewId) {
        checkPermissionService.checkTestReviewOwner(reviewId);
        testCaseReviewService.deleteCaseReview(reviewId);
    }

    @PostMapping("/list/all")
    public List<TestCaseReview> listAll() {
        return testCaseReviewService.listCaseReviewAll();
    }

    @PostMapping("/relevance")
    @MsAuditLog(module = "track_test_case_review", type = OperLogConstants.ASSOCIATE_CASE, content = "#msClass.getLogDetails(#request)", msClass = TestCaseReviewService.class)
    public void testReviewRelevance(@RequestBody ReviewRelevanceRequest request) {
        testCaseReviewService.testReviewRelevance(request);
    }

    @PostMapping("/projects")
    public List<Project> getProjectByReviewId(@RequestBody TestReviewRelevanceRequest request) {
        List<String> projectIds = testReviewProjectService.getProjectIdsByReviewId();
        request.setProjectIds(projectIds);
        return testReviewProjectService.getProject(request);
    }

    @PostMapping("/project/{goPage}/{pageSize}")
    public Pager<List<Project>> getProjectByReviewId(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody TestReviewRelevanceRequest request) {
        List<String> projectIds = testReviewProjectService.getProjectIdsByReviewId();
        request.setProjectIds(projectIds);
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, testReviewProjectService.getProject(request));
    }


    @GetMapping("/get/{reviewId}")
    public TestCaseReview getTestReview(@PathVariable String reviewId) {
        checkPermissionService.checkTestReviewOwner(reviewId);
        return testCaseReviewService.getTestReview(reviewId);
    }

    @PostMapping("/edit/status/{reviewId}")
    @RequiresPermissions(PermissionConstants.PROJECT_TRACK_REVIEW_READ_EDIT)
    public void editTestPlanStatus(@PathVariable String reviewId) {
        checkPermissionService.checkTestReviewOwner(reviewId);
        testCaseReviewService.editTestReviewStatus(reviewId);
    }

    @GetMapping("/list/all/relate/{type}")
    public List<TestReviewDTOWithMetric> listRelateAll(@PathVariable String type) {
        return testCaseReviewService.listRelateAll(type);
    }
}
