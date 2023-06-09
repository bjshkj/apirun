package io.apirun.base.mapper.ext;

import io.apirun.controller.request.WorkspaceRequest;
import io.apirun.dto.WorkspaceDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtWorkspaceMapper {

    List<WorkspaceDTO> getWorkspaceWithOrg(@Param("request") WorkspaceRequest request);
    List<String> getWorkspaceIdsByOrgId(@Param("orgId") String orgId);

    String getOrganizationIdById(String resourceID);

    List<WorkspaceDTO> findIdAndNameByOrganizationId(@Param("organizationId") String organizationId);
}
