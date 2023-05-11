package io.apirun.api.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.api.dto.APIReportResult;
import io.apirun.api.dto.DeleteAPIReportRequest;
import io.apirun.api.dto.QueryAPIReportRequest;
import io.apirun.api.service.APIReportService;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.dto.DashboardTestDTO;
import io.apirun.service.CheckPermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/report")
public class APIReportController {

    @Resource
    private APIReportService apiReportService;
    @Resource
    private CheckPermissionService checkPermissionService;

    @GetMapping("recent/{count}")
    public List<APIReportResult> recentTest(@PathVariable int count) {
        String currentWorkspaceId = SessionUtils.getCurrentWorkspaceId();
        QueryAPIReportRequest request = new QueryAPIReportRequest();
        request.setWorkspaceId(currentWorkspaceId);
        request.setUserId(SessionUtils.getUserId());
        PageHelper.startPage(1, count, true);
        return apiReportService.recentTest(request);
    }

    @GetMapping("/list/{testId}/{goPage}/{pageSize}")
    public Pager<List<APIReportResult>> listByTestId(@PathVariable String testId, @PathVariable int goPage, @PathVariable int pageSize) {
        checkPermissionService.checkApiTestOwner(testId);
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiReportService.listByTestId(testId));

    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<APIReportResult>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryAPIReportRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return PageUtils.setPageInfo(page, apiReportService.list(request));
    }

    @GetMapping("/get/{reportId}")
    public APIReportResult get(@PathVariable String reportId) {
        return apiReportService.get(reportId);
    }

    @PostMapping("/delete")
    public void delete(@RequestBody DeleteAPIReportRequest request) {
        apiReportService.delete(request);
    }

    @GetMapping("dashboard/tests")
    public List<DashboardTestDTO> dashboardTests() {
        return apiReportService.dashboardTests(SessionUtils.getCurrentWorkspaceId());
    }

    @PostMapping("/batch/delete")
    public void deleteAPIReportBatch(@RequestBody DeleteAPIReportRequest reportRequest) {
        apiReportService.deleteAPIReportBatch(reportRequest);
    }


}
