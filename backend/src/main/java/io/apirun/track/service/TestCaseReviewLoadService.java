package io.apirun.track.service;

import io.apirun.base.domain.*;
import io.apirun.base.mapper.*;
import io.apirun.base.mapper.ext.ExtTestCaseReviewLoadMapper;
import io.apirun.commons.constants.TestPlanStatus;
import io.apirun.controller.request.OrderRequest;
import io.apirun.dto.TestReviewLoadCaseDTO;
import io.apirun.performance.request.RunTestPlanRequest;
import io.apirun.performance.service.PerformanceTestService;
import io.apirun.track.request.testplan.LoadCaseReportRequest;
import io.apirun.track.request.testreview.TestReviewRequest;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseReviewLoadService {
    @Resource
    TestCaseReviewMapper testCaseReviewMapper;
    @Resource
    private TestCaseReviewLoadMapper testCaseReviewLoadMapper;
    @Resource
    private ExtTestCaseReviewLoadMapper extTestCaseReviewLoadMapper;
    @Resource
    private PerformanceTestService performanceTestService;
    @Resource
    private SqlSessionFactory sqlSessionFactory;
    @Resource
    private LoadTestReportMapper loadTestReportMapper;
    @Resource
    private LoadTestMapper loadTestMapper;

    public List<LoadTest> relevanceList(TestReviewRequest request) {
        List<String> ids = extTestCaseReviewLoadMapper.selectIdsNotInPlan(request.getProjectId(), request.getTestCaseReviewId());
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return performanceTestService.getLoadTestListByIds(ids);
    }

    public List<TestReviewLoadCaseDTO> list(TestReviewRequest request) {
        List<OrderRequest> orders = request.getOrders();
        if (orders == null || orders.size() < 1) {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setName("create_time");
            orderRequest.setType("desc");
            orders = new ArrayList<>();
            orders.add(orderRequest);
        }
        request.setOrders(orders);
        return extTestCaseReviewLoadMapper.selectTestReviewLoadCaseList(request);
    }

    public void relevanceCase(TestReviewRequest request) {
        List<String> caseIds = request.getCaseIds();
        String reviewId = request.getTestCaseReviewId();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        TestCaseReviewLoadMapper testCaseReviewLoadMapper = sqlSession.getMapper(TestCaseReviewLoadMapper.class);
        caseIds.forEach(id -> {
            TestCaseReviewLoad t = new TestCaseReviewLoad();
            t.setId(UUID.randomUUID().toString());
            t.setTestCaseReviewId(reviewId);
            t.setLoadCaseId(id);
            t.setCreateTime(System.currentTimeMillis());
            t.setUpdateTime(System.currentTimeMillis());
            testCaseReviewLoadMapper.insert(t);
        });
        TestCaseReview testCaseReview = testCaseReviewMapper.selectByPrimaryKey(request.getTestCaseReviewId());
        if (org.apache.commons.lang3.StringUtils.equals(testCaseReview.getStatus(), TestPlanStatus.Prepare.name())
                || org.apache.commons.lang3.StringUtils.equals(testCaseReview.getStatus(), TestPlanStatus.Completed.name())) {
            testCaseReview.setStatus(TestPlanStatus.Underway.name());
            testCaseReviewMapper.updateByPrimaryKey(testCaseReview);
        }
        sqlSession.flushStatements();
    }

    public void delete(String id) {
        TestCaseReviewLoadExample example = new TestCaseReviewLoadExample();
        example.createCriteria().andIdEqualTo(id);
        testCaseReviewLoadMapper.deleteByExample(example);
    }

    public String run(RunTestPlanRequest request) {
        String reportId = performanceTestService.run(request);
        TestCaseReviewLoad testCaseReviewLoad = new TestCaseReviewLoad();
        testCaseReviewLoad.setId(request.getTestPlanLoadId());
        testCaseReviewLoad.setLoadReportId(reportId);
        testCaseReviewLoadMapper.updateByPrimaryKeySelective(testCaseReviewLoad);
        return reportId;
    }
//???
    public Boolean isExistReport(LoadCaseReportRequest request) {
        String reportId = request.getReportId();
        String testPlanLoadCaseId = request.getTestPlanLoadCaseId();
        LoadTestReportExample example = new LoadTestReportExample();
        example.createCriteria().andIdEqualTo(reportId);
        List<LoadTestReport> loadTestReports = loadTestReportMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(loadTestReports)) {
            TestCaseReviewLoad testCaseReviewLoad = new TestCaseReviewLoad();
            testCaseReviewLoad.setId(testPlanLoadCaseId);
            testCaseReviewLoad.setLoadReportId("");
            testCaseReviewLoadMapper.updateByPrimaryKeySelective(testCaseReviewLoad);
            return false;
        }
        return true;
    }

    public void deleteByRelevanceProjectIds(String id, List<String> relevanceProjectIds) {
        LoadTestExample loadTestExample = new LoadTestExample();
        loadTestExample.createCriteria().andProjectIdIn(relevanceProjectIds);
        List<LoadTest> loadTests = loadTestMapper.selectByExample(loadTestExample);
        TestCaseReviewLoadExample example = new TestCaseReviewLoadExample();
        TestCaseReviewLoadExample.Criteria criteria = example.createCriteria().andTestCaseReviewIdEqualTo(id);
        if (!CollectionUtils.isEmpty(loadTests)) {
            List<String> ids = loadTests.stream().map(LoadTest::getId).collect(Collectors.toList());
            criteria.andLoadCaseIdNotIn(ids);
        }
        testCaseReviewLoadMapper.deleteByExample(example);
    }

    public void batchDelete(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        TestCaseReviewLoadExample example = new TestCaseReviewLoadExample();
        example.createCriteria().andIdIn(ids);
        testCaseReviewLoadMapper.deleteByExample(example);
    }

    public void update(TestCaseReviewLoad testCaseReviewLoad) {
        if (!StringUtils.isEmpty(testCaseReviewLoad.getId())) {
            testCaseReviewLoadMapper.updateByPrimaryKeySelective(testCaseReviewLoad);
        }
    }

    public List<String> getStatus(String reviewId) {
        return extTestCaseReviewLoadMapper.getStatusByreviewId(reviewId);
    }
}
