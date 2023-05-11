package io.apirun.api.dto.parse.swagger;

import lombok.Data;

@Data
public class SwaggerInfo {
    private String version;
    private String title;
    private String description;
    private String termsOfService;
}
