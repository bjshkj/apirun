package io.apirun.base.mapper;

import io.apirun.base.domain.ApiScenario;
import io.apirun.base.domain.ApiScenarioExample;
import io.apirun.base.domain.ApiScenarioWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ApiScenarioMapper {
    long countByExample(ApiScenarioExample example);

    int deleteByExample(ApiScenarioExample example);

    int deleteByPrimaryKey(String id);

    int insert(ApiScenarioWithBLOBs record);

    int insertSelective(ApiScenarioWithBLOBs record);

    List<ApiScenarioWithBLOBs> selectByExampleWithBLOBs(ApiScenarioExample example);

    List<ApiScenario> selectByExample(ApiScenarioExample example);

    ApiScenarioWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ApiScenarioWithBLOBs record, @Param("example") ApiScenarioExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiScenarioWithBLOBs record, @Param("example") ApiScenarioExample example);

    int updateByExample(@Param("record") ApiScenario record, @Param("example") ApiScenarioExample example);

    int updateByPrimaryKeySelective(ApiScenarioWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ApiScenarioWithBLOBs record);

    int updateByPrimaryKey(ApiScenario record);

    List<ApiScenarioWithBLOBs> selectByProjectId(String projectId);
}