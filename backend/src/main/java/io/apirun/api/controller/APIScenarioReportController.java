package io.apirun.api.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.api.dto.APIReportBatchRequest;
import io.apirun.api.dto.DeleteAPIReportRequest;
import io.apirun.api.dto.QueryAPIReportRequest;
import io.apirun.api.dto.automation.APIScenarioReportResult;
import io.apirun.api.dto.automation.ExecuteType;
import io.apirun.api.service.ApiScenarioReportService;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.security.GuestAccess;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/scenario/report")
public class APIScenarioReportController {

    @Resource
    private ApiScenarioReportService apiReportService;

    @GuestAccess
    @GetMapping("/get/{reportId}")
    public APIScenarioReportResult get(@PathVariable String reportId) {
        return apiReportService.get(reportId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<APIScenarioReportResult>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryAPIReportRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return PageUtils.setPageInfo(page, apiReportService.list(request));
    }

    @PostMapping("/update")
    public String update(@RequestBody APIScenarioReportResult node) {
        node.setExecuteType(ExecuteType.Saved.name());
        return apiReportService.update(node);
    }

    @PostMapping("/delete")
    @MsAuditLog(module = "api_automation_report", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#request.id)", msClass = ApiScenarioReportService.class)
    public void delete(@RequestBody DeleteAPIReportRequest request) {
        apiReportService.delete(request);
    }

    @PostMapping("/batch/delete")
    @MsAuditLog(module = "api_automation_report", type = OperLogConstants.BATCH_DEL, beforeEvent = "#msClass.getLogDetails(#reportRequest.ids)", msClass = ApiScenarioReportService.class)
    public void deleteAPIReportBatch(@RequestBody APIReportBatchRequest reportRequest) {
        apiReportService.deleteAPIReportBatch(reportRequest);
    }

}
