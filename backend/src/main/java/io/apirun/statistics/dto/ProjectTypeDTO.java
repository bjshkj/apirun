package io.apirun.statistics.dto;

import lombok.Data;

@Data
public class ProjectTypeDTO {
    private String projectType; //项目类型
    private Integer number;     //项目数量
    private Integer newNum;     //新增项目
}
