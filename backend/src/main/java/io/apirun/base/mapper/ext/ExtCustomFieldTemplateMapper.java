package io.apirun.base.mapper.ext;

import io.apirun.base.domain.CustomFieldTemplate;
import io.apirun.dto.CustomFieldTemplateDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCustomFieldTemplateMapper {
    List<String> getCustomFieldIds(@Param("templateId") String templateId);

    List<CustomFieldTemplateDao> list(@Param("request") CustomFieldTemplate request);
}
