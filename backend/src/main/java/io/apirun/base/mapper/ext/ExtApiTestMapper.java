package io.apirun.base.mapper.ext;

import io.apirun.api.dto.APITestResult;
import io.apirun.api.dto.QueryAPITestRequest;
import io.apirun.base.domain.ApiTest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ExtApiTestMapper {
    List<APITestResult> list(@Param("request") QueryAPITestRequest request);

    List<ApiTest> getApiTestByProjectId(String projectId);

    List<ApiTest> listByIds(@Param("ids") List<String> ids);

    int checkApiTestOwner(@Param("testId") String testId, @Param("projectIds") Set<String> projectIds);

}
