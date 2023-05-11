package io.apirun.base.mapper.ext;

import io.apirun.controller.request.ProjectRequest;
import io.apirun.dto.ProjectDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtProjectMapper {

    List<ProjectDTO> getProjectWithWorkspace(@Param("proRequest") ProjectRequest request);

    List<String> getProjectIdByWorkspaceId(String workspaceId);

    int removeIssuePlatform(@Param("platform") String platform, @Param("orgId") String orgId);

    List<ProjectDTO> getUserProject(@Param("proRequest") ProjectRequest request);

    List<ProjectDTO> getSwitchProject(@Param("proRequest") ProjectRequest request);

    List<ProjectDTO> getSwitchHttpRunnerProject(@Param("proRequest") ProjectRequest request);

    List<ProjectDTO> getProjects(@Param("projectType") String projectType,@Param("userId") String userId);

    List<ProjectDTO> getAllProjects(@Param("proRequest") ProjectRequest request);

    int getProjectPerssionByRequest(@Param("proRequest") ProjectRequest request);
}
