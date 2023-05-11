package io.apirun.controller.request;

import io.apirun.base.domain.CustomFieldTemplate;
import io.apirun.base.domain.IssueTemplate;
import lombok.Data;

import java.util.List;

@Data
public class UpdateIssueTemplateRequest extends IssueTemplate {
    List<CustomFieldTemplate> customFields;
}
