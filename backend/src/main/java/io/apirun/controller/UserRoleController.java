package io.apirun.controller;

import io.apirun.base.domain.Role;
import io.apirun.service.UserRoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RequestMapping("userrole")
@RestController
public class UserRoleController {

    @Resource
    private UserRoleService userRoleService;

    @GetMapping("/list/org/{orgId}/{userId}")
    public List<Role> getOrganizationMemberRoles(@PathVariable String orgId, @PathVariable String userId) {
        return userRoleService.getOrganizationMemberRoles(orgId, userId);
    }

    @GetMapping("/list/ws/{workspaceId}/{userId}")
    public List<Role> getWorkspaceMemberRoles(@PathVariable String workspaceId, @PathVariable String userId) {
        return userRoleService.getWorkspaceMemberRoles(workspaceId, userId);
    }

    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getUserRole(@PathVariable("userId") String userId) {
        return userRoleService.getUserRole(userId);
    }
}
