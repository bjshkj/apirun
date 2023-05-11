package io.apirun.api.dto;

import lombok.Data;

@Data
public class ApiMonitorRequest {

    private String url;
    private String startTime;
    private String endTime;
}