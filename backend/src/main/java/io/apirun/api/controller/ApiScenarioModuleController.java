package io.apirun.api.controller;

import io.apirun.api.dto.automation.ApiScenarioModuleDTO;
import io.apirun.api.dto.automation.DragApiScenarioModuleRequest;
import io.apirun.api.service.ApiScenarioModuleService;
import io.apirun.base.domain.ApiScenarioModule;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.CheckPermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/api/automation/module")
@RestController
public class ApiScenarioModuleController {

    @Resource
    ApiScenarioModuleService apiScenarioModuleService;
    @Resource
    private CheckPermissionService checkPermissionService;

    @GetMapping("/list/{projectId}")
    public List<ApiScenarioModuleDTO> getNodeByProjectId(@PathVariable String projectId) {
        checkPermissionService.checkProjectOwner(projectId);
        return apiScenarioModuleService.getNodeTreeByProjectId(projectId);
    }

    @PostMapping("/add")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.CREATE, title = "#node.name", content = "#msClass.getLogDetails(#node)", msClass = ApiScenarioModuleService.class)
    public String addNode(@RequestBody ApiScenarioModule node) {
        return apiScenarioModuleService.addNode(node);
    }

    @PostMapping("/edit")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#node)", title = "#node.name", content = "#msClass.getLogDetails(#node)", msClass = ApiScenarioModuleService.class)
    public int editNode(@RequestBody DragApiScenarioModuleRequest node) {
        return apiScenarioModuleService.editNode(node);
    }

    @GetMapping("/list/plan/{planId}")
    public List<ApiScenarioModuleDTO> getNodeByPlanId(@PathVariable String planId) {
        checkPermissionService.checkTestPlanOwner(planId);
        return apiScenarioModuleService.getNodeByPlanId(planId);
    }

    @PostMapping("/delete")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#nodeIds)", msClass = ApiScenarioModuleService.class)
    public int deleteNode(@RequestBody List<String> nodeIds) {
        //nodeIds 包含删除节点ID及其所有子节点ID
        return apiScenarioModuleService.deleteNode(nodeIds);
    }

    @PostMapping("/drag")
    @MsAuditLog(module = "api_automation", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#node)", title = "#node.name", content = "#msClass.getLogDetails(#node)", msClass = ApiScenarioModuleService.class)
    public void dragNode(@RequestBody DragApiScenarioModuleRequest node) {
        apiScenarioModuleService.dragNode(node);
    }

    @PostMapping("/pos")
    public void treeSort(@RequestBody List<String> ids) {
        apiScenarioModuleService.sort(ids);
    }

}
