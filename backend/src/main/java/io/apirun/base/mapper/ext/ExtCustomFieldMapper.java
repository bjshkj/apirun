package io.apirun.base.mapper.ext;

import io.apirun.base.domain.CustomField;
import io.apirun.controller.request.QueryCustomFieldRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCustomFieldMapper {

    List<CustomField> list(@Param("request") QueryCustomFieldRequest request);

    List<CustomField> listRelate(@Param("request") QueryCustomFieldRequest request);

    List<String> listIds(@Param("request") QueryCustomFieldRequest request);
}
