package io.apirun.base.mapper.ext;

import io.apirun.base.domain.Group;
import io.apirun.base.domain.User;
import io.apirun.controller.request.member.QueryMemberRequest;
import io.apirun.controller.request.organization.QueryOrgMemberRequest;
import io.apirun.dto.RelatedSource;
import io.apirun.dto.UserGroupDTO;
import io.apirun.dto.UserGroupHelpDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserGroupMapper {

    List<UserGroupDTO> getUserGroup(@Param("userId") String userId);

    List<Group> getOrganizationMemberGroups(@Param("orgId") String orgId, @Param("userId") String userId);

    List<User> getOrgMemberList(@Param("orgMember") QueryOrgMemberRequest request);

    List<Group> getWorkspaceMemberGroups(@Param("workspaceId") String workspaceId, @Param("userId") String userId);

    List<User> getMemberList(@Param("member") QueryMemberRequest request);

    List<UserGroupHelpDTO> getUserRoleHelpList(@Param("userId") String userId);

    List<User> getProjectMemberList(@Param("request") QueryMemberRequest request);

    List<Group> getProjectMemberGroups(@Param("projectId") String projectId,@Param("userId") String userId);

    List<RelatedSource> getRelatedSource(@Param("userId") String userId);
}
