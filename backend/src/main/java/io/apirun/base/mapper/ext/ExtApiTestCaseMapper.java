package io.apirun.base.mapper.ext;

import io.apirun.api.dto.datacount.ApiDataCountResult;
import io.apirun.api.dto.definition.ApiTestCaseDTO;
import io.apirun.api.dto.definition.ApiTestCaseInfo;
import io.apirun.api.dto.definition.ApiTestCaseRequest;
import io.apirun.api.dto.definition.ApiTestCaseResult;
import io.apirun.base.domain.ApiTestCase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtApiTestCaseMapper {

    List<ApiTestCaseResult> list(@Param("request") ApiTestCaseRequest request);

    List<ApiTestCaseDTO> listSimple(@Param("request") ApiTestCaseRequest request);

    List<String> selectIdsNotExistsInPlan(@Param("projectId") String projectId, @Param("planId") String planId);
    List<String> selectIdsNotExistsInReview(@Param("projectId") String projectId, @Param("reviewId") String reviewId);


    List<ApiDataCountResult> countProtocolByProjectID(String projectId);

    long countByProjectIDAndCreateInThisWeek(@Param("projectId") String projectId, @Param("firstDayTimestamp") long firstDayTimestamp, @Param("lastDayTimestamp") long lastDayTimestamp);

    List<ApiTestCaseInfo> getRequest(@Param("request") ApiTestCaseRequest request);

    ApiTestCase getNextNum(@Param("definitionId") String definitionId);

    ApiTestCaseInfo selectApiCaseInfoByPrimaryKey(String id);

    List<ApiTestCase> selectEffectiveTestCaseByProjectId(String projectId);

    List<String> idSimple(@Param("request") ApiTestCaseRequest request);

    List<ApiTestCaseInfo> getCaseInfo(@Param("request") ApiTestCaseRequest request);
}
