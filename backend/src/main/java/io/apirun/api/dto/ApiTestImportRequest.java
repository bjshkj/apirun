package io.apirun.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiTestImportRequest {
    private String name;
    private String id;
    private String moduleId;
    private String modulePath;
    private String environmentId;
    private String projectId;
    private String platform;
    private Boolean useEnvironment;
    private String swaggerUrl;
    private String fileName;
    //导入策略
    private String modeId;
    private String userId;
    //调用类型
    private String type;
}
