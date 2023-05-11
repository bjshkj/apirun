package io.apirun.base.mapper.ext;

import io.apirun.api.dto.datacount.ApiDataCountResult;
import io.apirun.api.dto.definition.ApiComputeResult;
import io.apirun.api.dto.definition.ApiDefinitionRequest;
import io.apirun.api.dto.definition.ApiDefinitionResult;
import io.apirun.api.dto.definition.ApiSwaggerUrlDTO;
import io.apirun.base.domain.ApiDefinition;
import io.apirun.base.domain.ApiDefinitionExample;
import io.apirun.controller.request.BaseQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtApiDefinitionMapper {
    List<ApiSwaggerUrlDTO> selectScheduleList(@Param("projectId") String projectId);

    List<ApiDefinitionResult> list(@Param("request") ApiDefinitionRequest request);

    int moduleCount(@Param("request") ApiDefinitionRequest request);

    //List<ApiComputeResult> selectByIds(@Param("ids") List<String> ids);

    List<ApiComputeResult> selectByIds(@Param("ids") List<String> ids, @Param("projectId") String projectId);

    int removeToGc(@Param("ids") List<String> ids);

    int removeToGcByExample(ApiDefinitionExample example);

    int reduction(@Param("ids") List<String> ids);

    List<ApiDataCountResult> countProtocolByProjectID(String projectId);

    Long countByProjectIDAndCreateInThisWeek(@Param("projectId") String projectId, @Param("firstDayTimestamp") long firstDayTimestamp, @Param("lastDayTimestamp") long lastDayTimestamp);

    List<ApiDataCountResult> countStateByProjectID(String projectId);

    List<ApiDataCountResult> countApiCoverageByProjectID(String projectId);

    ApiDefinition getNextNum(@Param("projectId") String projectId);

    List<ApiDefinitionResult> listRelevance(@Param("request") ApiDefinitionRequest request);

    List<ApiDefinitionResult> listRelevanceReview(@Param("request") ApiDefinitionRequest request);

    List<String> selectIds(@Param("request") BaseQueryRequest query);

    List<ApiDefinition> selectEffectiveIdByProjectId(String projectId);

    List<ApiDefinitionResult> listByIds(@Param("ids") List<String> ids);
}
