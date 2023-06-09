package io.apirun.api.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.api.dto.*;
import io.apirun.api.dto.automation.*;
import io.apirun.api.dto.automation.parse.ScenarioImport;
import io.apirun.api.dto.definition.RunDefinitionRequest;
import io.apirun.api.service.ApiAutomationService;
import io.apirun.base.domain.ApiScenario;
import io.apirun.base.domain.ApiScenarioWithBLOBs;
import io.apirun.base.domain.Schedule;
import io.apirun.commons.constants.ApiRunMode;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.constants.PermissionConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.controller.request.ScheduleRequest;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.track.request.testcase.ApiCaseRelevanceRequest;
import io.apirun.track.request.testplan.FileOperationRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/automation")
public class ApiAutomationController {

    @Resource
    ApiAutomationService apiAutomationService;

    @PostMapping("/list/{goPage}/{pageSize}")
    @RequiresPermissions("PROJECT_API_SCENARIO:READ")
    public Pager<List<ApiScenarioDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ApiScenarioRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return PageUtils.setPageInfo(page, apiAutomationService.list(request));
    }

    @PostMapping("/list/all")
    @RequiresPermissions("PROJECT_API_SCENARIO:READ")
    public List<ApiScenarioWithBLOBs> listAll(@RequestBody ApiScenarioBatchRequest request) {
        return apiAutomationService.listAll(request);
    }

    @PostMapping("/listWithIds/all")
    @RequiresPermissions("PROJECT_API_SCENARIO:READ")
    public List<ApiScenarioWithBLOBs> listWithIds(@RequestBody ApiScenarioBatchRequest request) {
        return apiAutomationService.listWithIds(request);
    }


    @PostMapping("/id/all")
    @RequiresPermissions("PROJECT_API_SCENARIO:READ")
    public List<String> idAll(@RequestBody ApiScenarioBatchRequest request) {
        return apiAutomationService.idAll(request);
    }

    @GetMapping("/list/{projectId}")
    @RequiresPermissions("PROJECT_API_SCENARIO:READ")
    public List<ApiScenarioDTO> list(@PathVariable String projectId) {
        ApiScenarioRequest request = new ApiScenarioRequest();
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        request.setProjectId(projectId);
        return apiAutomationService.list(request);
    }

    @PostMapping(value = "/create")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.CREATE, title = "#request.name", content = "#msClass.getLogDetails(#request.id)", msClass = ApiAutomationService.class)
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_CREATE)
    public ApiScenario create(@RequestPart("request") SaveApiScenarioRequest request, @RequestPart(value = "bodyFiles") List<MultipartFile> bodyFiles,
                              @RequestPart(value = "scenarioFiles") List<MultipartFile> scenarioFiles) {
        return apiAutomationService.create(request, bodyFiles, scenarioFiles);
    }

    @PostMapping(value = "/update")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#request.id)", title = "#request.name", content = "#msClass.getLogDetails(#request.id)", msClass = ApiAutomationService.class)
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_EDIT)
    public void update(@RequestPart("request") SaveApiScenarioRequest request, @RequestPart(value = "bodyFiles") List<MultipartFile> bodyFiles,
                       @RequestPart(value = "scenarioFiles") List<MultipartFile> scenarioFiles) {
        apiAutomationService.update(request, bodyFiles, scenarioFiles);
    }

    @GetMapping("/delete/{id}")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#id)", msClass = ApiAutomationService.class)
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_DELETE)
    public void delete(@PathVariable String id) {
        apiAutomationService.delete(id);
    }

    @PostMapping("/deleteBatch")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.BATCH_DEL, beforeEvent = "#msClass.getLogDetails(#ids)", msClass = ApiAutomationService.class)
    public void deleteBatch(@RequestBody List<String> ids) {
        apiAutomationService.deleteBatch(ids);
    }

    @PostMapping("/deleteBatchByCondition")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.BATCH_DEL, beforeEvent = "#msClass.getLogDetails(#request.ids)", msClass = ApiAutomationService.class)
    public void deleteBatchByCondition(@RequestBody ApiScenarioBatchRequest request) {
        apiAutomationService.deleteBatchByCondition(request);
    }

    @PostMapping("/removeToGc")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.GC, beforeEvent = "#msClass.getLogDetails(#ids)", msClass = ApiAutomationService.class)
    public void removeToGc(@RequestBody List<String> ids) {
        apiAutomationService.removeToGc(ids);
    }

    @PostMapping("/removeToGcByBatch")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.BATCH_GC, beforeEvent = "#msClass.getLogDetails(#request.ids)", msClass = ApiAutomationService.class)
    public void removeToGcByBatch(@RequestBody ApiScenarioBatchRequest request) {
        apiAutomationService.removeToGcByBatch(request);
    }

    @PostMapping("/reduction")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.RESTORE, beforeEvent = "#msClass.getLogDetails(#ids)", msClass = ApiAutomationService.class)
    public void reduction(@RequestBody List<String> ids) {
        apiAutomationService.reduction(ids);
    }

    @GetMapping("/getApiScenario/{id}")
    public ApiScenarioWithBLOBs getScenarioDefinition(@PathVariable String id) {
        return apiAutomationService.getApiScenario(id);
    }

    @PostMapping("/getApiScenarioEnv")
    public ScenarioEnv getScenarioDefinition(@RequestBody ApiScenarioEnvRequest request) {
        return apiAutomationService.getApiScenarioEnv(request.getDefinition());
    }

    @GetMapping("/getApiScenarioProjectId/{id}")
    public ScenarioEnv getApiScenarioProjectId(@PathVariable String id) {
        return apiAutomationService.getApiScenarioProjectId(id);
    }

    @PostMapping("/getApiScenarioProjectIdByConditions")
    public List<ScenarioIdProjectInfo> getApiScenarioProjectIdByConditions(@RequestBody ApiScenarioBatchRequest request) {
        return apiAutomationService.getApiScenarioProjectIdByConditions(request);
    }

    @PostMapping("/getApiScenarios")
    public List<ApiScenarioWithBLOBs> getApiScenarios(@RequestBody List<String> ids) {
        return apiAutomationService.getApiScenarios(ids);
    }

    @PostMapping(value = "/run/debug")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.DEBUG, title = "#request.scenarioName", project = "#request.projectId")
    public void runDebug(@RequestPart("request") RunDefinitionRequest request,
                         @RequestPart(value = "bodyFiles") List<MultipartFile> bodyFiles, @RequestPart(value = "scenarioFiles") List<MultipartFile> scenarioFiles) {
        request.setExecuteType(ExecuteType.Debug.name());
        apiAutomationService.debugRun(request, bodyFiles, scenarioFiles);
    }

    @PostMapping(value = "/run")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.EXECUTE, content = "#msClass.getLogDetails(#request.ids)", msClass = ApiAutomationService.class)
    public String run(@RequestBody RunScenarioRequest request) {
        request.setExecuteType(ExecuteType.Completed.name());
        request.setTriggerMode(ApiRunMode.SCENARIO.name());
        request.setRunMode(ApiRunMode.SCENARIO.name());
        return apiAutomationService.run(request);
    }

    @PostMapping(value = "/run/jenkins")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.EXECUTE, content = "#msClass.getLogDetails(#request.id)", msClass = ApiAutomationService.class)
    public String runByJenkins(@RequestBody RunScenarioRequest request) {
        request.setExecuteType(ExecuteType.Saved.name());
        request.setTriggerMode(ApiRunMode.API.name());
        request.setRunMode(ApiRunMode.SCENARIO.name());
        return apiAutomationService.run(request);
    }

    @PostMapping(value = "/run/batch")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.EXECUTE, content = "#msClass.getLogDetails(#request.ids)", msClass = ApiAutomationService.class)
    public String runBatch(@RequestBody RunScenarioRequest request) {
        request.setExecuteType(ExecuteType.Saved.name());
        request.setTriggerMode(ApiRunMode.SCENARIO.name());
        request.setRunMode(ApiRunMode.SCENARIO.name());
        return apiAutomationService.run(request);
    }

    @PostMapping("/batch/edit")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_EDIT)
    @MsAuditLog(module = "api_automation", type = OperLogConstants.BATCH_UPDATE, beforeEvent = "#msClass.getLogDetails(#request.ids)", content = "#msClass.getLogDetails(#request.ids)", msClass = ApiAutomationService.class)
    public void bathEdit(@RequestBody ApiScenarioBatchRequest request) {
        apiAutomationService.bathEdit(request);
    }

    @PostMapping("/batch/update/env")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_EDIT)
    @MsAuditLog(module = "api_automation", type = OperLogConstants.BATCH_UPDATE, beforeEvent = "#msClass.getLogDetails(#request.ids)", content = "#msClass.getLogDetails(#request.ids)", msClass = ApiAutomationService.class)
    public void batchUpdateEnv(@RequestBody ApiScenarioBatchRequest request) {
        apiAutomationService.batchUpdateEnv(request);
    }

    @PostMapping("/getReference")
    public ReferenceDTO getReference(@RequestBody ApiScenarioRequest request) {
        return apiAutomationService.getReference(request);
    }

    @PostMapping("/scenario/plan")
    public String addScenarioToPlan(@RequestBody SaveApiPlanRequest request) {
        return apiAutomationService.addScenarioToPlan(request);
    }

    @PostMapping("/relevance")
    @MsAuditLog(module = "track_test_plan", type = OperLogConstants.ASSOCIATE_CASE, content = "#msClass.getLogDetails(#request)", msClass = ApiAutomationService.class)
    public void testPlanRelevance(@RequestBody ApiCaseRelevanceRequest request) {
        apiAutomationService.relevance(request);
    }

    @PostMapping("/relevance/review")
    public void testCaseReviewRelevance(@RequestBody ApiCaseRelevanceRequest request) {
        apiAutomationService.relevanceReview(request);
    }

    @PostMapping(value = "/schedule/update")
    public void updateSchedule(@RequestBody Schedule request) {
        apiAutomationService.updateSchedule(request);
    }

    @PostMapping(value = "/schedule/create")
    public void createSchedule(@RequestBody ScheduleRequest request) {
        apiAutomationService.createSchedule(request);
    }

    @PostMapping(value = "/genPerformanceTestJmx")
    public JmxInfoDTO genPerformanceTestJmx(@RequestBody RunScenarioRequest runRequest) throws Exception {
        runRequest.setExecuteType(ExecuteType.Completed.name());
        return apiAutomationService.genPerformanceTestJmx(runRequest);
    }

    @PostMapping("/file/download")
    public ResponseEntity<byte[]> download(@RequestBody FileOperationRequest fileOperationRequest) {
        byte[] bytes = apiAutomationService.loadFileAsBytes(fileOperationRequest);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileOperationRequest.getName() + "\"")
                .body(bytes);
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_IMPORT_SCENARIO)
    @MsAuditLog(module = "api_automation", type = OperLogConstants.IMPORT, sourceId = "#request.id", title = "#request.name", project = "#request.projectId")
    public ScenarioImport scenarioImport(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("request") ApiTestImportRequest request) {
        return apiAutomationService.scenarioImport(file, request);
    }

    @PostMapping(value = "/export")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_EXPORT_SCENARIO)
    @MsAuditLog(module = "api_automation", type = OperLogConstants.EXPORT, sourceId = "#request.id", title = "#request.name", project = "#request.projectId")
    public ApiScenrioExportResult export(@RequestBody ApiScenarioBatchRequest request) {
        return apiAutomationService.export(request);
    }

    @PostMapping(value = "/export/jmx")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ_EXPORT_SCENARIO)
    @MsAuditLog(module = "api_automation", type = OperLogConstants.EXPORT, sourceId = "#request.id", title = "#request.name", project = "#request.projectId")
    public List<ApiScenrioExportJmx> exportJmx(@RequestBody ApiScenarioBatchRequest request) {
        return apiAutomationService.exportJmx(request);
    }

    //修复场景前置脚本中的第三方库导入
    @PostMapping(value = "/fix/crypto/import")
    public void fixCryptoImport(@RequestPart("projectId") String projectId) {
        apiAutomationService.fixUpdate(projectId);
    }

}

