package io.apirun.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class Issues implements Serializable {
    private String id;

    private String title;

    private String status;

    private Long createTime;

    private Long updateTime;

    private String reporter;

    private String lastmodify;

    private String platform;

    private String projectId;

    private String creator;

    private Integer num;

    private String resourceId;

    private static final long serialVersionUID = 1L;
}