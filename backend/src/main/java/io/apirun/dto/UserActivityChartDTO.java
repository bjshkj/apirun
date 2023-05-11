package io.apirun.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserActivityChartDTO {
    private List<String> xList;
    private List<Object> yList;
}
