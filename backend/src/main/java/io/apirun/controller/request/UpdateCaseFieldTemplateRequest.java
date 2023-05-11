package io.apirun.controller.request;

import io.apirun.base.domain.CustomFieldTemplate;
import io.apirun.base.domain.TestCaseTemplateWithBLOBs;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCaseFieldTemplateRequest extends TestCaseTemplateWithBLOBs {
    List<CustomFieldTemplate> customFields;
}
