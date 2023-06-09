package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.Organization;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.controller.request.OrganizationRequest;
import io.apirun.dto.OrganizationMemberDTO;
import io.apirun.dto.OrganizationResource;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.OrganizationService;
import io.apirun.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RequestMapping("organization")
@RestController
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;
    @Resource
    private UserService userService;

    @PostMapping("/add")
    @MsAuditLog(module = "system_organization", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#organization.id)", msClass = OrganizationService.class)
    public Organization addOrganization(@RequestBody Organization organization) {
        organization.setId(UUID.randomUUID().toString());
        return organizationService.addOrganization(organization);
    }

    @GetMapping("/list")
    public List<Organization> getOrganizationList() {
        return organizationService.getOrganizationList(new OrganizationRequest());
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<Organization>> getOrganizationList(@RequestBody OrganizationRequest request, @PathVariable int goPage, @PathVariable int pageSize) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, organizationService.getOrganizationList(request));
    }

    @GetMapping("/delete/{organizationId}")
    @MsAuditLog(module = "system_organization", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#organizationId)", msClass = OrganizationService.class)
    public void deleteOrganization(@PathVariable(value = "organizationId") String organizationId) {
        userService.refreshSessionUser("organization", organizationId);
        organizationService.deleteOrganization(organizationId);
    }

    @PostMapping("/update")
    @MsAuditLog(module = "system_organization", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#organization.id)", content = "#msClass.getLogDetails(#organization.id)", msClass = OrganizationService.class)
    public void updateOrganization(@RequestBody Organization organization) {
        organizationService.updateOrganization(organization);
    }

    @GetMapping("/list/userorg/{userId}")
    public List<Organization> getOrganizationListByUserId(@PathVariable String userId) {
        return organizationService.getOrganizationListByUserId(userId);
    }

    @PostMapping("/member/update")
    @MsAuditLog(module = "organization_member", type = OperLogConstants.UPDATE,beforeEvent = "#msClass.getLogDetails(#memberDTO)", content = "#msClass.getLogDetails(#memberDTO)", msClass = OrganizationService.class)
    public void updateOrgMember(@RequestBody OrganizationMemberDTO memberDTO) {
        organizationService.updateOrgMember(memberDTO);
    }

    @GetMapping("/list/resource/{groupId}/{type}")
    public OrganizationResource listResource(@PathVariable String groupId, @PathVariable String type) {
        return organizationService.listResource(groupId, type);
    }
}
