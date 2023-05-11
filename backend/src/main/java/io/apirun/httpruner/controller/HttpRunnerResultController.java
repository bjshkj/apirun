package io.apirun.httpruner.controller;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.httpruner.dto.HttprunnerTaskSummaryDTO;
import io.apirun.httpruner.dto.TaskSummaryResult;
import io.apirun.httpruner.service.HttpRunnerResultService;
import io.apirun.base.domain.ApiTestReportLog;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.httpruner.service.HttpRunnerService;
import io.apirun.security.GuestAccess;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/httprunner/result")
@Log4j
public class HttpRunnerResultController {

    @Resource
    private HttpRunnerResultService httpRunnerResultService;
    @Resource
    private HttpRunnerService httpRunnerService;

    @GuestAccess
    @PostMapping("/summary")
    // 执行完成后，提交整体的执行结果
    public void submitTestResult(
            @RequestParam(value = "result") MultipartFile result) {
        // 解析结果，入库
        try {
            String res = new String(result.getBytes());
            TaskSummaryResult taskSummaryResult = JSON.parseObject(res, TaskSummaryResult.class);
            httpRunnerResultService.saveHttpRunnerResult(taskSummaryResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GuestAccess
    @GetMapping("/log/{reportId}/{goPage}")
    public Pager<List<ApiTestReportLog>> logs(@PathVariable String reportId, @PathVariable int goPage) {
        Page<Object> page = PageHelper.startPage(goPage, 1, true);
        return PageUtils.setPageInfo(page, httpRunnerResultService.getReportLogs(reportId));
    }

    @GetMapping("/log/download/{reportId}")
    public void downloadLog(@PathVariable String reportId, HttpServletResponse response) throws Exception {
        httpRunnerResultService.downloadLog(response, reportId);
    }

    @GuestAccess
    @GetMapping("/summary/info/{taskId}")
    public HttprunnerTaskSummaryDTO getSummaryInfo(@PathVariable String taskId) {
        return httpRunnerResultService.getSummaryInfo(taskId);
    }

    @GuestAccess
    @GetMapping("/testcases/{taskId}")
    public List<Map<String, Object>> getTestCaseResults(@PathVariable String taskId) {
        return httpRunnerResultService.getDetailReport(taskId);
    }

    @GetMapping("summary/delete/{taskId}")
    public void deleteSummary(@PathVariable String taskId){
        httpRunnerResultService.deleteSummary(taskId);
    }

}
