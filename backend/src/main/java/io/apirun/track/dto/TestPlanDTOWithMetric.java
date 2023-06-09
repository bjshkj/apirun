package io.apirun.track.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPlanDTOWithMetric extends TestPlanDTO {
    private Double passRate;
    private Double testRate;
    private Integer passed;
    private Integer tested;
    private Integer total;
}
