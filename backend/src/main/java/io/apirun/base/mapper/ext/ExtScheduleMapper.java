package io.apirun.base.mapper.ext;

import io.apirun.api.dto.datacount.response.TaskInfoResult;
import io.apirun.api.dto.definition.ApiSwaggerUrlDTO;
import io.apirun.controller.request.QueryScheduleRequest;
import io.apirun.dto.ScheduleDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtScheduleMapper {
    List<ScheduleDao> list(@Param("request") QueryScheduleRequest request);

    long countTaskByProjectId(String workspaceId);

    long countTaskByProjectIdAndCreateTimeRange(@Param("projectId")String projectId, @Param("startTime") long startTime, @Param("endTime") long endTime);

    List<TaskInfoResult> findRunningTaskInfoByProjectID(@Param("projectId") String workspaceID, @Param("types") List<String> typeFilter);

    void insert(@Param("apiSwaggerUrlDTO") ApiSwaggerUrlDTO apiSwaggerUrlDTO);

    ApiSwaggerUrlDTO  select(String id);

    int updateNameByResourceID(@Param("resourceId") String resourceId, @Param("name") String name);

}