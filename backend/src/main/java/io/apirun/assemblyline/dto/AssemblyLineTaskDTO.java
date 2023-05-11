package io.apirun.assemblyline.dto;

import lombok.Data;
@Data
public class AssemblyLineTaskDTO {
    private String projectId; //项目id
    private String taskId;  //流水线任务id
    private String taskName;  //流水线任务名称
    private String email;//邮箱
}
