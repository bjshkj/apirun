package io.apirun.commoninterface.service;

import io.apirun.base.domain.IssuesDao;
import io.apirun.base.domain.TestPlan;
import io.apirun.base.domain.User;
import io.apirun.base.mapper.UserMapper;
import io.apirun.base.mapper.ext.*;
import io.apirun.common.pojo.ResponseParam;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.commons.utils.ServiceUtils;
import io.apirun.controller.request.ProjectRequest;
import io.apirun.i18n.Translator;
import io.apirun.track.dto.TestCaseReviewDTO;
import io.apirun.track.dto.TestPlanDTO;
import io.apirun.track.dto.TestPlanReportDTO;
import io.apirun.track.request.report.QueryTestPlanReportRequest;
import io.apirun.track.request.testcase.IssuesRequest;
import io.apirun.track.request.testcase.QueryTestPlanRequest;
import io.apirun.track.request.testreview.QueryCaseReviewRequest;
import io.apirun.track.service.TestPlanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TestTraceGeelibService {

    @Resource
    ExtTestPlanMapper extTestPlanMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    ExtProjectMapper extProjectMapper;

    @Resource
    ExtTestCaseReviewMapper extTestCaseReviewMapper;

    @Resource
    ExtIssuesMapper extIssuesMapper;

    @Autowired
    TestPlanService testPlanService;

    @Resource
    ExtTestPlanReportMapper extTestPlanReportMapper;

    public List<TestPlanDTO>  getTestPlanList(QueryTestPlanRequest queryTestPlanRequest){
        return  extTestPlanMapper.getPlanList(queryTestPlanRequest);
    }

    public int getProjectPerssionByRequest(ProjectRequest request){
       return extProjectMapper.getProjectPerssionByRequest(request);
    }

    public ResponseParam checkPerssion(String projectId, String email) {
        ResponseParam responseParam = new ResponseParam();
        if(projectId.isEmpty() || email.isEmpty()){
            LogUtil.error("project字段或email字段为空");
            responseParam.setCode(1100);
            responseParam.setMsg("project字段或email字段为空");
            return responseParam;
        }
        User user = userMapper.selectByEmail(email);
        if(user == null){
            LogUtil.error("当前系统无此用户");
            responseParam.setCode(1100);
            responseParam.setMsg("当前系统无此用户");
            return responseParam;
        }
        ProjectRequest pre = new ProjectRequest();
        pre.setUserId(user.getId());
        pre.setProjectId(projectId);
        int project = getProjectPerssionByRequest(pre);
        if(project <=0){
            LogUtil.error("当前用户无此项目的权限");
            responseParam.setCode(1100);
            responseParam.setMsg("当前用户无此项目的权限");
            return responseParam;
        }
        return responseParam;
    }

    public List<TestCaseReviewDTO> listCaseReview(QueryCaseReviewRequest request) {
        request.setOrders(ServiceUtils.getDefaultOrder(request.getOrders()));
        String projectId = request.getProjectId();
        if (StringUtils.isBlank(projectId)) {
            return new ArrayList<>();
        }
        return extTestCaseReviewMapper.list(request);
    }

    public List<IssuesDao> listIssues(IssuesRequest request) {
        request.setOrders(ServiceUtils.getDefaultOrder(request.getOrders()));
        List<IssuesDao> issues = extIssuesMapper.getIssuesByProjectId(request);

        List<String> ids = issues.stream()
                .map(IssuesDao::getCreator)
                .collect(Collectors.toList());
        Map<String, User> userMap = ServiceUtils.getUserMap(ids);
        List<String> resourceIds = issues.stream()
                .map(IssuesDao::getResourceId)
                .collect(Collectors.toList());

        List<TestPlan> testPlans = testPlanService.getTestPlanByIds(resourceIds);
        Map<String, String> planMap = testPlans.stream()
                .collect(Collectors.toMap(TestPlan::getId, TestPlan::getName));

        issues.forEach(item -> {
            User createUser = userMap.get(item.getCreator());
            if (createUser != null) {
                item.setCreatorName(createUser.getName());
            }
            if (planMap.get(item.getResourceId()) != null) {
                item.setResourceName(planMap.get(item.getResourceId()));
            }
        });

        return issues;
    }


    public List<TestPlanReportDTO> listTestPlanReport(QueryTestPlanReportRequest request) {
        request.setOrders(ServiceUtils.getDefaultOrder(request.getOrders()));
        List<TestPlanReportDTO> returnList = extTestPlanReportMapper.list(request);
        return returnList;
    }
}
