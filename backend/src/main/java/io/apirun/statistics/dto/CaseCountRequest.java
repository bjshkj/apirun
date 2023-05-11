package io.apirun.statistics.dto;

import lombok.Data;

@Data
public class CaseCountRequest {
    private String workspaceId; //工作空间id
    private Object beginTime;   //开始时间
    private Object endTime;     //结束时间
    private String dataSpan;    //时间间隔
    private String operType;    //操作类型
    private String caseType;    //用例类型
    private String projectType; //项目类型
}
