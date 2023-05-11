package io.apirun.api.dto.parse;

import io.apirun.api.dto.scenario.Scenario;
import lombok.Data;

import java.util.List;

@Data
public class ApiImport {
    private List<Scenario> scenarios;
}
