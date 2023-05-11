package io.apirun.base.mapper.ext;

import io.apirun.dto.AssemblyLineScenarioDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtAssemblyLineScenarioMapper {
    List<AssemblyLineScenarioDTO> getAssemblyLineScenarios(@Param("project_id") String project_id);
    List<AssemblyLineScenarioDTO> searchAssemblyLineScenarios(@Param("project_id") String project_id,@Param("query") String query);
}
