package io.apirun.base.domain;

import io.apirun.performance.dto.MonitorTargetType;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoadTestMonitorData implements Serializable {
    private Long id;

    private String reportId;

    private String target;

    private MonitorTargetType targetType;

    private String pointerType;

    private String numberIcalUnit;

    private String monitorName;

    private Long startTime;

    private Long endTime;

    private String targetLabel;

    private static final long serialVersionUID = 1L;
}