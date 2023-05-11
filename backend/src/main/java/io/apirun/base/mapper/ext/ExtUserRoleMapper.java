package io.apirun.base.mapper.ext;

import io.apirun.base.domain.Role;
import io.apirun.base.domain.User;
import io.apirun.controller.request.UserRequest;
import io.apirun.controller.request.member.QueryMemberRequest;
import io.apirun.controller.request.organization.QueryOrgMemberRequest;
import io.apirun.dto.OrganizationMemberDTO;
import io.apirun.dto.UserRoleHelpDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserRoleMapper {

    List<UserRoleHelpDTO> getUserRoleHelpList(@Param("userId") String userId);

    List<User> getMemberList(@Param("member") QueryMemberRequest request);

    List<User> getOrgMemberList(@Param("orgMember") QueryOrgMemberRequest request);

    List<OrganizationMemberDTO> getOrganizationMemberDTO(@Param("orgMember") QueryOrgMemberRequest request);

    List<Role> getOrganizationMemberRoles(@Param("orgId") String orgId, @Param("userId") String userId);

    List<Role> getWorkspaceMemberRoles(@Param("workspaceId") String workspaceId, @Param("userId") String userId);

    List<User> getBesideOrgMemberList(@Param("orgId") String orgId);


    List<User> getTestManagerAndTestUserList(@Param("request") QueryMemberRequest request);

    List<String> selectIdsByQuery(@Param("organizationId") String organizationId, @Param("orgMember")UserRequest condition);
}
