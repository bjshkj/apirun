package io.apirun.base.mapper.ext;

import io.apirun.base.domain.HttprunnerTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtHttprunnerTaskMapper {

    List<HttprunnerTask> getTaskListByProjectId(@Param("projectId") String projectId);

    List<HttprunnerTask> getNeedExecuteTask(@Param("taskStatus") List<String> taskStatus);

    int updateTaskStatus( HttprunnerTask record);

    List<HttprunnerTask> getTaskByNodeIdAndTaskStatus(String nodeId ,String status);

    int getExecutingTaskCountByNodeId(String nodeId ,String status);
}