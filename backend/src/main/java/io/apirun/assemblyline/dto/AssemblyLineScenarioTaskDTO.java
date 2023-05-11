package io.apirun.assemblyline.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssemblyLineScenarioTaskDTO extends AssemblyLineTaskDTO{
    private List<AssemblyLineScenarioEnvDTO> scenarioEnv;
}
