package io.apirun.dto;

import io.apirun.base.domain.CustomFieldTemplate;
import lombok.Data;

@Data
public class CustomFieldTemplateDao extends CustomFieldTemplate {
    private String name;

    private String scene;

    private String type;

    private String remark;

    private String options;

    private Boolean system;
}
