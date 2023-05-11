package io.apirun.base.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiTestReportLog implements Serializable {
    private String id;

    private String reportId;

    private Long part;

    private String content;

    private static final long serialVersionUID = 1L;
}