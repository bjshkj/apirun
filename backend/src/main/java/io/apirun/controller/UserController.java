package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.User;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.user.SessionUser;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.controller.request.member.AddMemberRequest;
import io.apirun.controller.request.member.EditPassWordRequest;
import io.apirun.controller.request.member.QueryMemberRequest;
import io.apirun.controller.request.member.UserRequest;
import io.apirun.controller.request.organization.AddOrgMemberRequest;
import io.apirun.controller.request.organization.QueryOrgMemberRequest;
import io.apirun.controller.request.resourcepool.UserBatchProcessRequest;
import io.apirun.dto.*;
import io.apirun.excel.domain.ExcelResponse;
import io.apirun.i18n.Translator;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.CheckPermissionService;
import io.apirun.service.OrganizationService;
import io.apirun.service.UserService;
import io.apirun.service.WorkspaceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("user")
@RestController
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private WorkspaceService workspaceService;
    @Resource
    private CheckPermissionService checkPermissionService;

    @PostMapping("/special/add")
    @MsAuditLog(module = "system_user", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#user)", msClass = UserService.class)
    public UserDTO insertUser(@RequestBody UserRequest user) {
        return userService.insert(user);
    }

    @PostMapping("/special/list/{goPage}/{pageSize}")
    public Pager<List<User>> getUserList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody io.apirun.controller.request.UserRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getUserListWithRequest(request));
    }

    @GetMapping("/special/user/role/{userId}")
    public UserRoleDTO getUserRole(@PathVariable("userId") String userId) {
        return userService.getUserRole(userId);
    }

    @GetMapping("/special/user/group/{userId}")
    public UserGroupPermissionDTO getUserGroup(@PathVariable("userId") String userId) {
        return userService.getUserGroup(userId);
    }

    @GetMapping("/special/delete/{userId}")
    @MsAuditLog(module = "system_user", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#userId)", msClass = UserService.class)
    public void deleteUser(@PathVariable(value = "userId") String userId) {
        userService.deleteUser(userId);
        // 踢掉在线用户
        SessionUtils.kickOutUser(userId);
    }

    @PostMapping("/special/update")
    @MsAuditLog(module = "system_user", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#user)", content = "#msClass.getLogDetails(#user)", msClass = UserService.class)
    public void updateUser(@RequestBody UserRequest user) {
        userService.updateUserRole(user);
    }

    @PostMapping("/special/update_status")
    @MsAuditLog(module = "system_user", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#user.id)", content = "#msClass.getLogDetails(#user.id)", msClass = UserService.class)
    public void updateStatus(@RequestBody User user) {
        userService.updateUser(user);
    }

    @PostMapping("/special/ws/member/list/{goPage}/{pageSize}")
    public Pager<List<User>> getMemberListByAdmin(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getMemberList(request));
    }

    @PostMapping("/special/ws/member/list/all")
    public List<User> getMemberListByAdmin(@RequestBody QueryMemberRequest request) {
        return userService.getMemberList(request);
    }

    @PostMapping("/special/ws/member/add")
    @MsAuditLog(module = "workspace_member", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#request.userIds,#request.workspaceId)", msClass = UserService.class)
    public void addMemberByAdmin(@RequestBody AddMemberRequest request) {
        userService.addMember(request);
    }

    @GetMapping("/special/ws/member/delete/{workspaceId}/{userId}")
    @MsAuditLog(module = "workspace_member", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#userId)", msClass = UserService.class)
    public void deleteMemberByAdmin(@PathVariable String workspaceId, @PathVariable String userId) {
        userService.deleteMember(workspaceId, userId);
    }

    @PostMapping("/special/org/member/add")
    @MsAuditLog(module = "organization_member", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#request.userIds,#request.organizationId)", msClass = UserService.class)
    public void addOrganizationMemberByAdmin(@RequestBody AddOrgMemberRequest request) {
        userService.addOrganizationMember(request);
    }

    @GetMapping("/special/org/member/delete/{organizationId}/{userId}")
    @MsAuditLog(module = "organization_member", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#userId)", msClass = UserService.class)
    public void delOrganizationMemberByAdmin(@PathVariable String organizationId, @PathVariable String userId) {
        userService.delOrganizationMember(organizationId, userId);
    }

    @PostMapping("/special/org/member/list/{goPage}/{pageSize}")
    public Pager<List<User>> getOrgMemberListByAdmin(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryOrgMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getOrgMemberList(request));
    }

    @PostMapping("/special/org/member/list/all")
    public List<User> getOrgMemberListByAdmin(@RequestBody QueryOrgMemberRequest request) {
        return userService.getOrgMemberList(request);
    }

    @GetMapping("/list")
    public List<User> getUserList() {
        return userService.getUserList();
    }

    @PostMapping("/update/current")
    @MsAuditLog(module = "personal_information_personal_settings", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#user.id)", content = "#msClass.getLogDetails(#user.id)", msClass = UserService.class)
    public UserDTO updateCurrentUser(@RequestBody User user) {
        String currentUserId = SessionUtils.getUserId();
        if (!StringUtils.equals(currentUserId, user.getId())) {
            MSException.throwException(Translator.get("not_authorized"));
        }
        userService.updateUser(user);
        UserDTO userDTO = userService.getUserDTO(user.getId());
        SessionUtils.putUser(SessionUser.fromUser(userDTO));
        return SessionUtils.getUser();
    }

    @PostMapping("/switch/source/org/{sourceId}")
    public UserDTO switchOrganization(@PathVariable(value = "sourceId") String sourceId) {
        userService.switchUserRole("organization", sourceId);
        return SessionUtils.getUser();
    }

    @PostMapping("/switch/source/ws/{sourceId}")
    public UserDTO switchWorkspace(@PathVariable(value = "sourceId") String sourceId) {
        userService.switchUserRole("workspace", sourceId);
        return SessionUtils.getUser();
    }

    @PostMapping("/refresh/{sign}/{sourceId}")
    public UserDTO refreshSessionUser(@PathVariable String sign, @PathVariable String sourceId) {
        userService.refreshSessionUser(sign, sourceId);
        return SessionUtils.getUser();
    }

    @GetMapping("/info/{userId}")
    public UserDTO getUserInfo(@PathVariable(value = "userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 获取工作空间成员用户
     */
    @PostMapping("/ws/member/list/{goPage}/{pageSize}")
    public Pager<List<User>> getMemberList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getMemberList(request));
    }

    @PostMapping("/project/member/list/{goPage}/{pageSize}")
    public Pager<List<User>> getProjectMemberList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getProjectMemberList(request));
    }

    @PostMapping("/project/member/list")
    public List<User> getProjectMemberListAll(@RequestBody QueryMemberRequest request) {
        return userService.getProjectMemberList(request);
    }

    /**
     * 获取工作空间成员用户 不分页
     */
    @PostMapping("/ws/member/list/all")
    public List<User> getMemberList(@RequestBody QueryMemberRequest request) {
        return userService.getMemberList(request);
    }

    /**
     * 添加工作空间成员
     */
    @PostMapping("/ws/member/add")
    @MsAuditLog(module = "workspace_member", type = OperLogConstants.CREATE, title = "添加工作空间成员")
    public void addMember(@RequestBody AddMemberRequest request) {
        String wsId = request.getWorkspaceId();
//        workspaceService.checkWorkspaceOwner(wsId);
        userService.addMember(request);
    }

    @PostMapping("/project/member/add")
//    @MsAuditLog(module = "workspace_member", type = OperLogConstants.CREATE, title = "添加项目成员成员")
    public void addProjectMember(@RequestBody AddMemberRequest request) {
//        workspaceService.checkWorkspaceOwner(wsId);
        userService.addProjectMember(request);
    }

    /**
     * 删除工作空间成员
     */
    @GetMapping("/ws/member/delete/{workspaceId}/{userId}")
    @MsAuditLog(module = "workspace_member", type = OperLogConstants.DELETE, title = "删除工作空间成员")
    public void deleteMember(@PathVariable String workspaceId, @PathVariable String userId) {
//        workspaceService.checkWorkspaceOwner(workspaceId);
        String currentUserId = SessionUtils.getUser().getId();
        if (StringUtils.equals(userId, currentUserId)) {
            MSException.throwException(Translator.get("cannot_remove_current"));
        }
        userService.deleteMember(workspaceId, userId);
    }

    @GetMapping("/project/member/delete/{projectId}/{userId}")
//    @MsAuditLog(module = "workspace_member", type = OperLogConstants.DELETE, title = "删除工作空间成员")
    public void deleteProjectMember(@PathVariable String projectId, @PathVariable String userId) {
//        workspaceService.checkWorkspaceOwner(workspaceId);
//        String currentUserId = SessionUtils.getUser().getId();
//        if (StringUtils.equals(userId, currentUserId)) {
//            MSException.throwException(Translator.get("cannot_remove_current"));
//        }
        userService.deleteProjectMember(projectId, userId);
    }

    /**
     * 添加组织成员
     */
    @PostMapping("/org/member/add")
    @MsAuditLog(module = "organization_member", type = OperLogConstants.CREATE, title = "'添加组织成员-'+#request.userIds")
    public void addOrganizationMember(@RequestBody AddOrgMemberRequest request) {
        organizationService.checkOrgOwner(request.getOrganizationId());
        userService.addOrganizationMember(request);
    }

    /**
     * 删除组织成员
     */
    @GetMapping("/org/member/delete/{organizationId}/{userId}")
    @MsAuditLog(module = "organization_member", type = OperLogConstants.DELETE, title = "删除组织成员")
    public void delOrganizationMember(@PathVariable String organizationId, @PathVariable String userId) {
        organizationService.checkOrgOwner(organizationId);
        String currentUserId = SessionUtils.getUser().getId();
        if (StringUtils.equals(userId, currentUserId)) {
            MSException.throwException(Translator.get("cannot_remove_current"));
        }
        userService.delOrganizationMember(organizationId, userId);
    }

    /**
     * 查询组织成员列表
     */
    @PostMapping("/org/member/list/{goPage}/{pageSize}")
    public Pager<List<User>> getOrgMemberList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryOrgMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getOrgMemberList(request));
    }

    /**
     * 组织成员列表不分页
     */
    @PostMapping("/org/member/list/all")
    public List<User> getOrgMemberList(@RequestBody QueryOrgMemberRequest request) {
        return userService.getOrgMemberList(request);
    }

    @GetMapping("/besideorg/list/{orgId}")
    public List<User> getBesideOrgMemberList(@PathVariable String orgId) {
        return userService.getBesideOrgMemberList(orgId);
    }

    /*
     * 修改当前用户密码
     * */
    @PostMapping("/update/password")
    @MsAuditLog(module = "system_user", type = OperLogConstants.UPDATE, title = "个人密码")
    public int updateCurrentUserPassword(@RequestBody EditPassWordRequest request) {
        return userService.updateCurrentUserPassword(request);
    }

    /*管理员修改用户密码*/
    @PostMapping("/special/password")
    @MsAuditLog(module = "system_user", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#request.id)", content = "#msClass.getLogDetails(#request.id)", msClass = UserService.class)
    public int updateUserPassword(@RequestBody EditPassWordRequest request) {
        return userService.updateUserPassword(request);
    }

    /**
     * 获取工作空间成员用户 不分页
     */
    @PostMapping("/ws/member/tester/list")
    public List<User> getTestManagerAndTestUserList(@RequestBody QueryMemberRequest request) {
        return userService.getTestManagerAndTestUserList(request);
    }

    @GetMapping("/search/{condition}")
    public List<User> searchUser(@PathVariable String condition) {
        return userService.searchUser(condition);
    }

    @GetMapping("/export/template")
    public void testCaseTemplateExport(HttpServletResponse response) {
        userService.userTemplateExport(response);
    }

    @PostMapping("/import/{userId}")
    @MsAuditLog(module = "system_user", type = OperLogConstants.IMPORT)
    public ExcelResponse testCaseImport(MultipartFile file, @PathVariable String userId, HttpServletRequest request) {
        return userService.userImport(file, userId, request);
    }

    @PostMapping("/special/batchProcessUserInfo")
    @MsAuditLog(module = "system_user", type = OperLogConstants.BATCH_UPDATE, beforeEvent = "#msClass.getLogDetails(#request)", content = "#msClass.getLogDetails(#request)", msClass = UserService.class)
    public String batchProcessUserInfo(@RequestBody UserBatchProcessRequest request) {
        String returnString = "success";
        userService.batchProcessUserInfo(request);
        return returnString;
    }

    @GetMapping("/getWorkspaceDataStruct/{organizationId}")
    public List<CascaderDTO> getWorkspaceDataStruct(@PathVariable String organizationId) {
        List<OrganizationMemberDTO> organizationList = organizationService.findIdAndNameByOrganizationId(organizationId);
        List<WorkspaceDTO> workspaceDTOList = workspaceService.findIdAndNameByOrganizationId(organizationId);
        if (!workspaceDTOList.isEmpty()) {
            Map<String, List<WorkspaceDTO>> orgIdWorkspaceMap = workspaceDTOList.stream().collect(Collectors.groupingBy(WorkspaceDTO::getOrganizationId));
            List<CascaderDTO> returnList = CascaderParse.parseWorkspaceDataStruct(organizationList, orgIdWorkspaceMap);
            return returnList;
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/getUserRoleDataStruct/{organizationId}")
    public List<CascaderDTO> getUserRoleDataStruct(@PathVariable String organizationId) {
        List<OrganizationMemberDTO> organizationList = organizationService.findIdAndNameByOrganizationId(organizationId);
        List<WorkspaceDTO> workspaceDTOList = workspaceService.findIdAndNameByOrganizationId(organizationId);
        if (!workspaceDTOList.isEmpty()) {
            Map<String, List<WorkspaceDTO>> orgIdWorkspaceMap = workspaceDTOList.stream().collect(Collectors.groupingBy(WorkspaceDTO::getOrganizationId));
            List<CascaderDTO> returnList = CascaderParse.parseUserRoleDataStruct(organizationList, orgIdWorkspaceMap, false);
            return returnList;
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/getWorkspaceUserRoleDataStruct/{organizationId}")
    public List<CascaderDTO> getWorkspaceUserRoleDataStruct(@PathVariable String organizationId) {
        List<OrganizationMemberDTO> organizationList = organizationService.findIdAndNameByOrganizationId(organizationId);
        List<WorkspaceDTO> workspaceDTOList = workspaceService.findIdAndNameByOrganizationId(organizationId);
        if (!workspaceDTOList.isEmpty()) {
            Map<String, List<WorkspaceDTO>> orgIdWorkspaceMap = workspaceDTOList.stream().collect(Collectors.groupingBy(WorkspaceDTO::getOrganizationId));
            List<CascaderDTO> returnList = CascaderParse.parseUserRoleDataStruct(organizationList, orgIdWorkspaceMap, true);
            return returnList;
        } else {
            return new ArrayList<>();
        }
    }


}
