package io.apirun.dto;

import io.apirun.base.domain.TestCaseTemplateWithBLOBs;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseTemplateDao extends TestCaseTemplateWithBLOBs {
    List<CustomFieldDao> customFields;
}
