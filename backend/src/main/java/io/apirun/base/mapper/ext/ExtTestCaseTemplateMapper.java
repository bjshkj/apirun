package io.apirun.base.mapper.ext;

import io.apirun.base.domain.TestCaseTemplateWithBLOBs;
import io.apirun.controller.request.BaseQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ExtTestCaseTemplateMapper {
    List<TestCaseTemplateWithBLOBs> list(@Param("request") BaseQueryRequest request);
}
