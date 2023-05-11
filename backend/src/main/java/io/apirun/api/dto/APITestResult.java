package io.apirun.api.dto;

import io.apirun.base.domain.ApiTest;
import io.apirun.base.domain.Schedule;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class APITestResult extends ApiTest {

    private String projectName;

    private String userName;

    private Schedule schedule;
}
