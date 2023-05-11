package io.apirun.statistics.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChartDataDTO {
    private List<String> xlist;
    private List<CasesValuesDTO> ylists;
}
