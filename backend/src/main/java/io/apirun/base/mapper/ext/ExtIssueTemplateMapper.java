package io.apirun.base.mapper.ext;

import io.apirun.base.domain.IssueTemplate;
import io.apirun.controller.request.BaseQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtIssueTemplateMapper {
    List<IssueTemplate> list(@Param("request") BaseQueryRequest request);
}
