package io.apirun.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class NodeRegisterInfo implements Serializable {
    private String nodeId;

    private String ip;

    private String secretKey;

    private Integer maxLoad;

    private String type;

    private String nodeStatus;

    private String statusMsg;

    private Integer totalTasks;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;
}