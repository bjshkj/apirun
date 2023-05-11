package io.apirun.api.dto;

import io.apirun.api.dto.scenario.Scenario;
import io.apirun.base.domain.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SaveAPITestRequest {

    private String id;

    private String projectId;

    private String name;

    private List<Scenario> scenarioDefinition;

    private String userId;

    private Schedule schedule;

    private String triggerMode;

    private List<String> bodyUploadIds;
}
