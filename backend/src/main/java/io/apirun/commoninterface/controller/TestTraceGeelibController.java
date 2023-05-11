package io.apirun.commoninterface.controller;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import io.apirun.base.domain.IssuesDao;
import io.apirun.base.domain.User;
import io.apirun.base.mapper.UserMapper;
import io.apirun.common.pojo.ResponseParam;
import io.apirun.commoninterface.service.TestTraceGeelibService;

import io.apirun.commons.utils.PageUtils;
import io.apirun.controller.BaseController;
import io.apirun.controller.request.ProjectRequest;
import io.apirun.dto.ProjectDTO;
import io.apirun.security.GuestAccess;
import io.apirun.service.ProjectService;
import io.apirun.track.dto.TestCaseDTO;
import io.apirun.track.dto.TestCaseReviewDTO;
import io.apirun.track.dto.TestPlanDTO;
import io.apirun.track.dto.TestPlanReportDTO;
import io.apirun.track.request.report.QueryTestPlanReportRequest;
import io.apirun.track.request.testcase.IssuesRequest;
import io.apirun.track.request.testcase.QueryTestCaseRequest;
import io.apirun.track.request.testcase.QueryTestPlanRequest;
import io.apirun.track.request.testreview.QueryCaseReviewRequest;
import io.apirun.track.service.TestCaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/tracegeelib")
public class TestTraceGeelibController extends BaseController {

    @Autowired
    ProjectService projectService;

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestTraceGeelibService testTraceGeelibService;

    @Resource
    UserMapper userMapper;

    @GuestAccess
    @GetMapping(value = "/getProjectListByName/{goPage}/{pageSize}")
    public void getProjectListByName(@PathVariable int goPage, @PathVariable int pageSize, HttpServletRequest request, HttpServletResponse response) {
        ResponseParam responseParam = new ResponseParam();
        Map<String, Object> result = Maps.newHashMap();
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        User user = userMapper.selectByEmail(email);
        ProjectRequest projectRequest = new ProjectRequest();
        if(StringUtils.isNotEmpty(name)){
            projectRequest.setName(name);
        }
        if(user!=null){
            projectRequest.setUserId(user.getId());
        }
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        List<ProjectDTO> allProject = projectService.getAllProject(projectRequest);
        result.put("project",PageUtils.setPageInfo(page,allProject));
        responseParam.setData(result);
        sendJson(genMessageJson(responseParam.getCode(), responseParam.getMsg(), responseParam.getData()), response);
    }

    @GuestAccess
    @GetMapping("/listTraceCase/{goPage}/{pageSize}")
    public void listTraceCase(@PathVariable int goPage, @PathVariable int pageSize, HttpServletRequest request, HttpServletResponse response) {
        ResponseParam responseParam = new ResponseParam();
        Map<String, Object> result = Maps.newHashMap();
        String email = request.getParameter("email");
        String projectId = request.getParameter("projectId");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        responseParam = testTraceGeelibService.checkPerssion(projectId,email);
        if(responseParam.getCode() == 0){
            QueryTestCaseRequest queryTestCaseRequest = new QueryTestCaseRequest();
            queryTestCaseRequest.setProjectId(projectId);
            queryTestCaseRequest.setName(name);
            if (id != null) {
                queryTestCaseRequest.setTestCaseIds(Collections.singletonList(id));
            }
            Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
            /**由于数据库中的CreateUser是用户id，极库云这边需要用户名，所以转换一下*/
            List<TestCaseDTO> testCaseList = testCaseService.listTestCase(queryTestCaseRequest);
            testCaseList.forEach(item -> {
                User user = this.userMapper.selectByPrimaryKey(item.getCreateUser());
                if(user != null) {
                    item.setCreateUser(user.getName());
                }
            });
            result.put("testCaseList",PageUtils.setPageInfo(page,testCaseList));
            responseParam.setData(result);
        }
        sendJson(genMessageJson(responseParam.getCode(), responseParam.getMsg(), responseParam.getData()), response);

    }

    @GuestAccess
    @GetMapping("/listTestPlan/{goPage}/{pageSize}")
    public void listTestPlan(@PathVariable int goPage, @PathVariable int pageSize, HttpServletRequest request, HttpServletResponse response) {
        ResponseParam responseParam = new ResponseParam();
        Map<String, Object> result = Maps.newHashMap();
        String email = request.getParameter("email");
        String projectId = request.getParameter("projectId");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        responseParam = testTraceGeelibService.checkPerssion(projectId,email);
        if(responseParam.getCode() == 0) {
            QueryTestPlanRequest queryTestPlanRequest = new QueryTestPlanRequest();
            queryTestPlanRequest.setProjectId(projectId);
            queryTestPlanRequest.setName(name);
            if (id != null) {
                queryTestPlanRequest.setPlanIds(Collections.singletonList(id));
            }
            Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
            List<TestPlanDTO> testPlanList = testTraceGeelibService.getTestPlanList(queryTestPlanRequest);
            /**由于数据库中的Creator和Principal是用户id，极库云这边需要用户名，所以转换一下*/
            testPlanList.forEach(item -> {
                User creator = this.userMapper.selectByPrimaryKey(item.getCreator());
                if (creator != null) {
                    item.setCreator(creator.getName());
                }
                User principal = this.userMapper.selectByPrimaryKey(item.getPrincipal());
                if (principal != null) {
                    item.setPrincipal(principal.getName());
                }
            });
            result.put("testPlanList",PageUtils.setPageInfo(page,testPlanList));
            responseParam.setData(result);
        }
        sendJson(genMessageJson(responseParam.getCode(), responseParam.getMsg(), responseParam.getData()), response);
    }

    @GuestAccess
    @GetMapping("/listTestCaseReview/{goPage}/{pageSize}")
    public void listTestCaseReview(@PathVariable int goPage, @PathVariable int pageSize, HttpServletRequest request, HttpServletResponse response){
        ResponseParam responseParam = new ResponseParam();
        Map<String, Object> result = Maps.newHashMap();
        String email = request.getParameter("email");
        String projectId = request.getParameter("projectId");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        responseParam = testTraceGeelibService.checkPerssion(projectId,email);
        if(responseParam.getCode() == 0) {
            QueryCaseReviewRequest queryCaseReviewRequest = new QueryCaseReviewRequest();
            queryCaseReviewRequest.setProjectId(projectId);
            queryCaseReviewRequest.setName(name);
            queryCaseReviewRequest.setReviewId(id);
            Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
            List<TestCaseReviewDTO> testCaseReviewList = testTraceGeelibService.listCaseReview(queryCaseReviewRequest);
            result.put("testCaseReviewList",PageUtils.setPageInfo(page, testCaseReviewList));
            responseParam.setData(result);
        }
        sendJson(genMessageJson(responseParam.getCode(), responseParam.getMsg(), responseParam.getData()), response);
    }

    @GuestAccess
    @GetMapping("/listIssues/{goPage}/{pageSize}")
    public void listIssues(@PathVariable int goPage, @PathVariable int pageSize, HttpServletRequest request, HttpServletResponse response){
        ResponseParam responseParam = new ResponseParam();
        Map<String, Object> result = Maps.newHashMap();
        String email = request.getParameter("email");
        String projectId = request.getParameter("projectId");
        String title = request.getParameter("title");
        responseParam = testTraceGeelibService.checkPerssion(projectId,email);
        if(responseParam.getCode() == 0) {
            IssuesRequest issuesRequest = new IssuesRequest();
            issuesRequest.setProjectId(projectId);
            issuesRequest.setName(title);
            Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
            List<IssuesDao> issuesList = testTraceGeelibService.listIssues(issuesRequest);
            result.put("issuesList",PageUtils.setPageInfo(page, issuesList));
            responseParam.setData(result);
        }
        sendJson(genMessageJson(responseParam.getCode(), responseParam.getMsg(), responseParam.getData()), response);
    }

    @GuestAccess
    @GetMapping("/listTestPlanReport/{goPage}/{pageSize}")
    public void listTestPlanReport(@PathVariable int goPage, @PathVariable int pageSize, HttpServletRequest request, HttpServletResponse response) {
        ResponseParam responseParam = new ResponseParam();
        Map<String, Object> result = Maps.newHashMap();
        String email = request.getParameter("email");
        String projectId = request.getParameter("projectId");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        responseParam = testTraceGeelibService.checkPerssion(projectId, email);
        if (responseParam.getCode() == 0) {
            QueryTestPlanReportRequest queryTestPlanReportRequest = new QueryTestPlanReportRequest();
            queryTestPlanReportRequest.setProjectId(projectId);
            queryTestPlanReportRequest.setName(name);
            queryTestPlanReportRequest.setId(id);
            Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
            List<TestPlanReportDTO> testPlanReportList = testTraceGeelibService.listTestPlanReport(queryTestPlanReportRequest);
            result.put("testPlanReportList",PageUtils.setPageInfo(page, testPlanReportList));
            responseParam.setData(result);
        }
        sendJson(genMessageJson(responseParam.getCode(), responseParam.getMsg(), responseParam.getData()), response);
    }

}
