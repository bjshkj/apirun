package io.apirun.statistics.dto;

import lombok.Data;

import java.util.List;

@Data
public class CasesValuesDTO {
    private String operationType;
    private List<Integer> apiNum;
    private List<Integer> caseNum;
    private List<Integer> sceNum;
    private List<Integer> perNum;
    private List<Integer> httprunnerNum;
}
