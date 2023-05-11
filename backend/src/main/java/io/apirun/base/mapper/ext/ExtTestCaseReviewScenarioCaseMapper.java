package io.apirun.base.mapper.ext;

import io.apirun.api.dto.automation.ApiScenarioDTO;
import io.apirun.api.dto.automation.TestPlanScenarioRequest;
import io.apirun.base.domain.TestCaseReviewScenario;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtTestCaseReviewScenarioCaseMapper {
    void insertIfNotExists(@Param("request") TestCaseReviewScenario request);

    List<ApiScenarioDTO> list(@Param("request") TestPlanScenarioRequest request);

    List<String> getExecResultByReviewId(String reviewId);

    List<String> getIdsByReviewId(String reviewId);

    List<String> getNotRelevanceCaseIds(String planId, List<String> relevanceProjectIds);
}
