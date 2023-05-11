package io.apirun.api.dto.automation;

import io.apirun.base.domain.ApiScenario;
import io.apirun.track.dto.TestPlanDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReferenceDTO {
    List<ApiScenario> scenarioList;

    List<TestPlanDTO> testPlanList;
}
