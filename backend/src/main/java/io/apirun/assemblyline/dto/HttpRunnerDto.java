package io.apirun.assemblyline.dto;

import lombok.Data;

@Data
public class HttpRunnerDto {
    private String msg;   //消息
    private boolean status; //状态
    private String taskId; //任务id
}
