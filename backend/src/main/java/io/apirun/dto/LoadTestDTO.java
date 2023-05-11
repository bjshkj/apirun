package io.apirun.dto;

import io.apirun.base.domain.LoadTest;
import io.apirun.base.domain.Schedule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadTestDTO extends LoadTest {
    private String projectName;
    private String userName;
    private Schedule schedule;
}
