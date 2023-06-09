package io.apirun.base.mapper.ext;

import io.apirun.api.dto.automation.ApiScenarioDTO;
import io.apirun.api.dto.automation.TestPlanScenarioRequest;
import io.apirun.base.domain.TestPlanApiScenario;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtTestPlanScenarioCaseMapper {
    void insertIfNotExists(@Param("request") TestPlanApiScenario request);

    List<ApiScenarioDTO> list(@Param("request") TestPlanScenarioRequest request);

    List<String> getExecResultByPlanId(String planId);

    List<String> getIdsByPlanId(String planId);

    List<String> getNotRelevanceCaseIds(String planId, List<String> relevanceProjectIds);

    List<String> selectIds(@Param("request")TestPlanScenarioRequest request);

    List<TestPlanApiScenario> selectByIds(@Param("ids")String ids ,@Param("oderId")String oderId );

}