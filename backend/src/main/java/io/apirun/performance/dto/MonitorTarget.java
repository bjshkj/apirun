package io.apirun.performance.dto;

import lombok.Data;

@Data
public class MonitorTarget {

    private String id;
    private String label;
    private MonitorTargetType targetType;

}
