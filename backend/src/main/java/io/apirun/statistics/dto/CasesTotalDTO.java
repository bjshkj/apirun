package io.apirun.statistics.dto;

import lombok.Data;

@Data
public class CasesTotalDTO {
    private Long apiNum;  //接口数
    private Long caseNum;   //接口用例数
    private Long sceneNum;  //场景用例数
    private Long perNum;    //性能用例数
    private Long httpRunnerNum; //httpRunner用例数
}
