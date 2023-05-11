package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.Workspace;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.controller.request.WorkspaceRequest;
import io.apirun.dto.WorkspaceDTO;
import io.apirun.dto.WorkspaceMemberDTO;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.OrganizationService;
import io.apirun.service.UserService;
import io.apirun.service.WorkspaceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("workspace")
@RestController
public class WorkspaceController {
    @Resource
    private WorkspaceService workspaceService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private UserService userService;

    @PostMapping("add")
    @MsAuditLog(module = "system_workspace", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#workspace.id)", msClass = WorkspaceService.class)
    public Workspace addWorkspace(@RequestBody Workspace workspace) {
        String currentOrganizationId = SessionUtils.getCurrentOrganizationId();
        organizationService.checkOrgOwner(currentOrganizationId);
        return workspaceService.saveWorkspace(workspace);
    }

    @GetMapping("/list")
    public List<Workspace> getWorkspaceList() {
        return workspaceService.getWorkspaceList(new WorkspaceRequest());
    }

    @PostMapping("special/add")
    @MsAuditLog(module = "system_workspace", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#workspace.id)", msClass = WorkspaceService.class)
    public Workspace addWorkspaceByAdmin(@RequestBody Workspace workspace) {
        return workspaceService.addWorkspaceByAdmin(workspace);
    }

    @PostMapping("update")
    @MsAuditLog(module = "system_workspace", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#workspace.id)", content = "#msClass.getLogDetails(#workspace.id)", msClass = WorkspaceService.class)
    public Workspace updateWorkspace(@RequestBody Workspace workspace) {
//        workspaceService.checkWorkspaceOwnerByOrgAdmin(workspace.getId());
        return workspaceService.saveWorkspace(workspace);
    }

    @PostMapping("special/update")
    @MsAuditLog(module = "system_workspace", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#workspace.id)", content = "#msClass.getLogDetails(#workspace.id)", msClass = WorkspaceService.class)
    public void updateWorkspaceByAdmin(@RequestBody Workspace workspace) {
        workspaceService.updateWorkspaceByAdmin(workspace);
    }

    @GetMapping("special/delete/{workspaceId}")
    @MsAuditLog(module = "system_workspace", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#workspaceId)", msClass = WorkspaceService.class)
    public void deleteWorkspaceByAdmin(@PathVariable String workspaceId) {
        userService.refreshSessionUser("workspace", workspaceId);
        workspaceService.deleteWorkspace(workspaceId);
    }

    @GetMapping("delete/{workspaceId}")
    @MsAuditLog(module = "system_workspace", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#workspaceId)", msClass = WorkspaceService.class)
    public void deleteWorkspace(@PathVariable String workspaceId) {
//        workspaceService.checkWorkspaceOwnerByOrgAdmin(workspaceId);
        userService.refreshSessionUser("workspace", workspaceId);
        workspaceService.deleteWorkspace(workspaceId);
    }

    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<Workspace>> getWorkspaceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody WorkspaceRequest request) {
        request.setOrganizationId(SessionUtils.getCurrentOrganizationId());
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, workspaceService.getWorkspaceList(request));
    }

    @PostMapping("list/all/{goPage}/{pageSize}")
    public Pager<List<WorkspaceDTO>> getAllWorkspaceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody WorkspaceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, workspaceService.getAllWorkspaceList(request));
    }

    @GetMapping("/list/userworkspace/{userId}")
    public List<Workspace> getWorkspaceListByUserId(@PathVariable String userId) {
        return workspaceService.getWorkspaceListByUserId(userId);
    }

    @GetMapping("/list/orgworkspace/")
    public List<Workspace> getWorkspaceListByOrgIdAndUserId() {
        String currentOrganizationId = SessionUtils.getCurrentOrganizationId();
        return workspaceService.getWorkspaceListByOrgIdAndUserId(currentOrganizationId);
    }

    @PostMapping("/member/update")
    @MsAuditLog(module = "workspace_member", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#memberDTO)", content = "#msClass.getLogDetails(#memberDTO)", msClass = WorkspaceService.class)
    public void updateOrgMember(@RequestBody WorkspaceMemberDTO memberDTO) {
        workspaceService.updateWorkspaceMember(memberDTO);
    }
}
