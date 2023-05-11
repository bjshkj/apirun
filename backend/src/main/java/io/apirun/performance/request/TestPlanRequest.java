package io.apirun.performance.request;

import io.apirun.base.domain.Schedule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPlanRequest {

    private String id;

    private String projectId;

    private String name;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String loadConfiguration;

    private String advancedConfiguration;

    private String runtimeConfiguration;

    private Schedule schedule;

    private String testResourcePoolId;

    private static final long serialVersionUID = 1L;

}
