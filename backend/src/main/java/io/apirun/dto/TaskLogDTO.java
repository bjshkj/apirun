package io.apirun.dto;

import lombok.Data;

@Data
public class TaskLogDTO {

    private String taskId;
    private String time;
    private String msg;
    private String loggerName;
    private String level;
    private String path;
    private int lineno; // 为-1时，认为任务执行结束了
}
