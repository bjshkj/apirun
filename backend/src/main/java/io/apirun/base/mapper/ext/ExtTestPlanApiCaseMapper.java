package io.apirun.base.mapper.ext;

import io.apirun.api.dto.definition.ApiTestCaseRequest;
import io.apirun.api.dto.definition.TestPlanApiCaseDTO;
import io.apirun.base.domain.TestPlanApiCase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtTestPlanApiCaseMapper {
    void insertIfNotExists(@Param("request") TestPlanApiCase request);

    List<TestPlanApiCaseDTO> list(@Param("request") ApiTestCaseRequest request);

    List<String> getExecResultByPlanId(String planId);

    List<String> getIdsByPlanId(String planId);

    List<String> getNotRelevanceCaseIds(@Param("planId")String planId, @Param("relevanceProjectIds")List<String> relevanceProjectIds);

    List<String> getStatusByTestPlanId(String id);

    List<String> selectIds(@Param("request") ApiTestCaseRequest request);
}