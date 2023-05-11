package io.apirun.controller;

import io.apirun.base.domain.Group;
import io.apirun.base.domain.Organization;
import io.apirun.commons.utils.Pager;
import io.apirun.controller.request.GroupRequest;
import io.apirun.controller.request.group.EditGroupRequest;
import io.apirun.dto.GroupDTO;
import io.apirun.dto.GroupPermissionDTO;
import io.apirun.service.GroupService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RequestMapping("/user/group")
@RestController
public class GroupController {

    @Resource
    private GroupService groupService;

    @PostMapping("/get/{goPage}/{pageSize}")
    public Pager<List<GroupDTO>> getGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody EditGroupRequest request) {
        request.setGoPage(goPage);
        request.setPageSize(pageSize);
        return groupService.getGroupList(request);
    }

    @PostMapping("/get")
    public List<Group> getGroupByType(@RequestBody EditGroupRequest request) {
        return groupService.getGroupByType(request);
    }

    @PostMapping("/add")
    public Group addGroup(@RequestBody EditGroupRequest request) {
        return groupService.addGroup(request);
    }

    @PostMapping("/edit")
    public void editGroup(@RequestBody EditGroupRequest request) {
        groupService.editGroup(request);
    }

    @GetMapping("/delete/{id}")
    public void deleteGroup(@PathVariable String id) {
        groupService.deleteGroup(id);
    }

    @PostMapping("/permission")
    public GroupPermissionDTO getGroupResource(@RequestBody Group group) {
        return groupService.getGroupResource(group);
    }

    @PostMapping("/permission/edit")
    public void EditGroupPermission(@RequestBody EditGroupRequest editGroupRequest) {
        groupService.editGroupPermission(editGroupRequest);
    }

    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getAllUserGroup(@PathVariable("userId") String userId) {
        return groupService.getAllUserGroup(userId);
    }

    @PostMapping("/list")
    public List<Group> getGroupsByType(@RequestBody GroupRequest request) {
        return groupService.getGroupsByType(request);
    }

    @GetMapping("/list/org/{orgId}/{userId}")
    public List<Group> getOrganizationMemberGroups(@PathVariable String orgId, @PathVariable String userId) {
        return groupService.getOrganizationMemberGroups(orgId, userId);
    }

    @GetMapping("/list/ws/{workspaceId}/{userId}")
    public List<Group> getWorkspaceMemberGroups(@PathVariable String workspaceId, @PathVariable String userId) {
        return groupService.getWorkspaceMemberGroups(workspaceId, userId);
    }

    @GetMapping("/list/project/{projectId}/{userId}")
    public List<Group> getProjectMemberGroups(@PathVariable String projectId, @PathVariable String userId) {
        return groupService.getProjectMemberGroups(projectId, userId);
    }

    @GetMapping("/org/{userId}")
    public List<Organization> getOrganization(@PathVariable String userId) {
        return groupService.getOrganization(userId);
    }
}
