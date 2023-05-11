package io.apirun.base.domain;

import lombok.Data;

import java.util.List;

@Data
public class TestStepResult {

    private String taskId;
    private long testCaseId;
    private int index;
    private String name;
    private String path;
    private String method;
    private String status;
    private  String attachment;
    private String validateExtractor;
    private String validateScript;
    private String responseTimeMs;
    private String elapsedMs;
    private String contentSize;
    private ResponseInfo responseInfo;
    private RequestInfo requestInfo;

    @Data
    public static class ResponseInfo {
        private String url;
        private String headers;
        private boolean responseOk;
        private String statusCode;
        private String statusMsg;
        private String cookies;
        private String encoding;
        private String contentType;
        private String body;
    }

    @Data
    public static class RequestInfo {
        private String url;
        private String method;
        private String headers;
        private String body;
    }
}
