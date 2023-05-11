package io.apirun.performance.config;

import lombok.Data;
@Data
public class HulkConfig {

    private String secretKey;
    private String masterProjectId;
    private String childProjectId;

}