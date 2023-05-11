package io.apirun.base.mapper.ext;

import io.apirun.base.domain.FileMetadata;
import io.apirun.base.domain.LoadTest;
import io.apirun.dto.LoadTestDTO;
import io.apirun.performance.request.QueryProjectFileRequest;
import io.apirun.performance.request.QueryTestPlanRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ExtLoadTestMapper {
    List<LoadTestDTO> list(@Param("request") QueryTestPlanRequest params);

    List<LoadTest> getLoadTestByProjectId(String projectId);

    int checkLoadTestOwner(@Param("testId") String testId, @Param("projectIds") Set<String> projectIds);

    LoadTest getNextNum(@Param("projectId") String projectId);

    List<FileMetadata> getProjectFiles(@Param("projectId") String projectId, @Param("loadTypes") List<String> loadType,
                                       @Param("request") QueryProjectFileRequest request);

}
