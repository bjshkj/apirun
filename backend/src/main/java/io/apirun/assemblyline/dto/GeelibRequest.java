package io.apirun.assemblyline.dto;

import lombok.Data;

@Data
public class GeelibRequest {
    /**httprunner项目id*/
    private String id;
    /**httprunner项目分支*/
    private String branch;
    /**httprunner项目commit*/
    private String commit;
    /**任务执行路径，可以是单个yml文件，也可以是多个yml文件、还可以是文件夹、多个yml文件用逗号分隔*/
    private String execPath;
    /**env文件路径*/
    private String envPath;
    /**hosts文件路径*/
    private String hostsPath;
    /**任务名*/
    private String name;
    /**执行人*/
    private String email;
}
