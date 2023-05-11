package io.apirun.httpruner.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.apirun.base.mapper.HttprunnerTaskMapper;
import io.apirun.common.util.JsonUtil;
import io.apirun.common.util.ObjectToMapUtil;
import io.apirun.commons.exception.MSException;
import io.apirun.httpruner.dto.HttpRunnerTaskStatus;
import io.apirun.httpruner.dto.HttprunnerTaskSummaryDTO;
import io.apirun.httpruner.dto.TaskSummaryResult;
import io.apirun.base.domain.*;
import io.apirun.base.mapper.ApiTestReportLogMapper;
import io.apirun.base.mapper.HttpRunnerTestCaseResultMapper;
import io.apirun.base.mapper.HttprunnerTestStepResultMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HttpRunnerResultService {

    @Resource
    private HttpRunnerTestCaseResultMapper testCaseResultMapper;
    @Resource
    private HttprunnerTestStepResultMapper testStepResultMapper;
    @Resource
    private ApiTestReportLogMapper apiTestReportLogMapper;
    @Resource
    private HttprunnerTaskMapper HttprunnerTaskMapper;

    public void saveHttpRunnerResult(TaskSummaryResult summaryResult) {
        // 更新测试任务的状态和测试统计数据
        HttprunnerTask httprunnerTask = new HttprunnerTask();
        httprunnerTask.setId(summaryResult.getTaskId());
        httprunnerTask.setDuration(summaryResult.getDuration());
        httprunnerTask.setFtime(System.currentTimeMillis());
        httprunnerTask.setPlatform(summaryResult.getPlatform());
        httprunnerTask.setStime(summaryResult.getStartTime().getTime());
        httprunnerTask.setTcasesFail(summaryResult.getTestCaseStatInfo().getFailures());
        httprunnerTask.setTcasesSuccess(summaryResult.getTestCaseStatInfo().getSuccess());
        httprunnerTask.setTcasesTotal(summaryResult.getTestCaseStatInfo().getTotal());
        httprunnerTask.setTstepsErrors(summaryResult.getStepStatInfo().getErrors());
        httprunnerTask.setTstepsExpectedFailures(summaryResult.getStepStatInfo().getExpectedFailures());
        httprunnerTask.setTstepsFailures(summaryResult.getStepStatInfo().getFailures());
        httprunnerTask.setTstepsSkipped(summaryResult.getStepStatInfo().getSkipped());
        httprunnerTask.setTstepsSuccesses(summaryResult.getStepStatInfo().getSuccesses());
        httprunnerTask.setTstepsTotal(summaryResult.getStepStatInfo().getTotal());
        httprunnerTask.setTstepsUnexpectedSuccesses(summaryResult.getStepStatInfo().getUnexpectedSuccesses());
        HttprunnerTaskMapper.updateByPrimaryKeySelective(httprunnerTask);

        // 保存测试场景的测试数据
        int i = 0;
        for (TestCaseResult testCaseResult : summaryResult.getTestCaseResults()) {
            testCaseResult.setIndex(i);
            testCaseResultMapper.saveTestCaseResult(testCaseResult);
            // 保存每个场景下执行的step数据
            int j = 0;
            for (TestStepResult testStepResult : testCaseResult.getTestStepResults()) {
                testStepResult.setIndex(j);
                testStepResult.setTestCaseId(i);
                testStepResultMapper.saveTestStepResult(testStepResult);
                j += 1;
            }
            i += 1;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void savePartContent(String reportId, String content) {
        // TODO:更新状态

        LoadTestReportLogExample example = new LoadTestReportLogExample();
        LoadTestReportLogExample.Criteria criteria = example.createCriteria();
        criteria.andReportIdEqualTo(reportId);
        long part = apiTestReportLogMapper.countByExample(example);
        ApiTestReportLog record = new ApiTestReportLog();
        record.setReportId(reportId);
        record.setPart(part + 1);
        record.setContent(content);
        apiTestReportLogMapper.insert(record);
    }

    public List<ApiTestReportLog> getReportLogs(String reportId) {

        LoadTestReportLogExample example = new LoadTestReportLogExample();
        example.createCriteria().andReportIdEqualTo(reportId);
        example.setOrderByClause("part");
        return apiTestReportLogMapper.selectByExampleWithBLOBs(example);
    }

    public void downloadLog(HttpServletResponse response, String reportId) throws IOException {
        LoadTestReportLogExample example = new LoadTestReportLogExample();
        LoadTestReportLogExample.Criteria criteria = example.createCriteria();
        criteria.andReportIdEqualTo(reportId);
        example.setOrderByClause("part");

        long count = apiTestReportLogMapper.countByExample(example);

        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=apitest.log");
            for (long i = 1; i <= count; i++) {
                example.clear();
                LoadTestReportLogExample.Criteria innerCriteria = example.createCriteria();
                innerCriteria.andReportIdEqualTo(reportId).andPartEqualTo(i);

                List<ApiTestReportLog> apiTestReportLogs = apiTestReportLogMapper.selectByExampleWithBLOBs(example);
                ApiTestReportLog content = apiTestReportLogs.get(0);
                outputStream.write(content.getContent().getBytes());
                outputStream.flush();
            }
        }
    }

    public List<Map<String, Object>> getDetailReport(String reportId) {

        // 获取报告的数据，整合成需要的数据结构
        List<TestCaseResult> testCaseResults = testCaseResultMapper.selectByTaskId(reportId);
        if (testCaseResults == null) {
            return null;
        }
        List<Map<String, Object>> testCaseResultMaps = Lists.newArrayListWithCapacity(testCaseResults.size());

        // 填充测试步骤的结果
        for (TestCaseResult testcase : testCaseResults) {
            List<TestStepResult> testStepResults = testStepResultMapper.selectByTaskIdAndTestCaseIndex(reportId, testcase.getIndex());
            List<Map<String, Object>> testStepMaps = Lists.newArrayListWithCapacity(testStepResults.size());

            for (TestStepResult stepResult : testStepResults) {
                Map<String, Object> stepResultMap = ObjectToMapUtil.setConditionMap(stepResult);
                List<HashMap> validateExtractor = null;
                if (stepResult.getValidateExtractor() != null) {
                    validateExtractor = JsonUtil.jsonToObject(stepResult.getValidateExtractor(), new TypeReference<List<HashMap<String, Object>>>() {
                    });
                }
                stepResultMap.put("validateExtractor", validateExtractor == null ? Lists.newArrayListWithCapacity(1):validateExtractor);
                Map<String, Object> validateScript = Maps.newHashMap();
                if (stepResult.getValidateScript() != null) {
                    validateScript = JsonUtil.jsonToObject(stepResult.getValidateScript(), new TypeReference<HashMap<String, Object>>() {
                    });
                }
                stepResultMap.put("validateScript", validateScript);
                testStepMaps.add(stepResultMap);
            }

            Map<String, Object> testCaseMap = ObjectToMapUtil.setConditionMap(testcase);
            testCaseMap.put("testStepResults", testStepMaps);
            testCaseResultMaps.add(testCaseMap);
        }
        return testCaseResultMaps;
    }

    public HttprunnerTaskSummaryDTO getSummaryInfo(String taskId) {

        HttprunnerTaskSummaryDTO info = null;
        HttprunnerTask httprunnerTask = HttprunnerTaskMapper.selectByPrimaryKey(taskId);
        if (httprunnerTask != null) {
            info = new HttprunnerTaskSummaryDTO();
            info.setCreateUser(httprunnerTask.getCreateUser());
            info.setCtime(httprunnerTask.getCtime());
            info.setDuration(httprunnerTask.getDuration());
            info.setFtime(httprunnerTask.getFtime());
            info.setGitVerDesc(httprunnerTask.getGitVerDesc());
            info.setGitVersion(httprunnerTask.getGitVersion());
            info.setTstepsExpectedFailures(httprunnerTask.getTstepsExpectedFailures());
            info.setUtime(httprunnerTask.getUtime());
            info.setTstepsUnexpectedSuccesses(httprunnerTask.getTstepsUnexpectedSuccesses());
            info.setTstepsTotal(httprunnerTask.getTstepsTotal());
            info.setTstepsSuccesses(httprunnerTask.getTstepsSuccesses());
            info.setTstepsSkipped(httprunnerTask.getTstepsSkipped());
            info.setTstepsFailures(httprunnerTask.getTstepsFailures());
            info.setTstepsExpectedFailures(httprunnerTask.getTstepsExpectedFailures());
            info.setTstepsErrors(httprunnerTask.getTstepsErrors());
            info.setTcasesTotal(httprunnerTask.getTcasesTotal());
            info.setTcasesSuccess(httprunnerTask.getTcasesSuccess());
            info.setTcasesFail(httprunnerTask.getTcasesFail());
            info.setTaskStatus(httprunnerTask.getTaskStatus());
            info.setTaskName(httprunnerTask.getTaskName());
            info.setStime(httprunnerTask.getStime());

            if(httprunnerTask.getPlatform() != null && !httprunnerTask.getPlatform().trim().isEmpty()){
                Map<String, String> platfromInfo = JsonUtil.jsonToObject(httprunnerTask.getPlatform(), new TypeReference<HashMap<String, String>>() {
                });
                info.setPythonVersion(platfromInfo.getOrDefault("python_version", "--"));
                info.setPlatform(platfromInfo.getOrDefault("platform", "--"));
                info.setHttprunnerVersion(platfromInfo.getOrDefault("httprunner_version", "--"));
            }
        }
        return info;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSummary(String taskId){
        HttprunnerTask httprunnerTask = HttprunnerTaskMapper.selectByPrimaryKey(taskId);
        List<TestCaseResult> testCaseResultList = testCaseResultMapper.selectByTaskId(taskId);
        List<TestStepResult> testStepResultList = testStepResultMapper.selectByTaskId(taskId);
        List<ApiTestReportLog> apiTestReportLogList = apiTestReportLogMapper.selectByPrimaryKey(taskId);
        if (httprunnerTask != null && (httprunnerTask.getTaskStatus().equals("DONE") || httprunnerTask.getTaskStatus().equals("ERROR") ||
                httprunnerTask.getTaskStatus().equals("TIMEOUT"))) {
            //删除任务
            HttprunnerTaskMapper.deleteByPrimaryKey(taskId);
            if (!testCaseResultList.isEmpty()){
                //删除报告
                testCaseResultMapper.delTestCaseResultByTaskId(taskId);
            }
            if (!testStepResultList.isEmpty()){
                //删除报告步骤
                testStepResultMapper.delTestStepResultByTaskId(taskId);
            }
            if (!apiTestReportLogList.isEmpty()){
                //删除日志
                apiTestReportLogMapper.deleteByPrimaryKey(taskId);
            }
        }else {
            MSException.throwException("删除失败！任务正在执行中。");
        }
    }
}
