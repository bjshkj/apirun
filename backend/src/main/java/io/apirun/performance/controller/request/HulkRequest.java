package io.apirun.performance.controller.request;

import io.apirun.performance.config.HulkConfig;
import io.apirun.performance.dto.MonitorTarget;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HulkRequest {
    private String reportId;
    private Long startTime;
    private Long endTime;
    private HulkConfig hulkConfig;
    private List<MonitorTarget> monitorTargets;
}
