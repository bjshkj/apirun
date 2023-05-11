package io.apirun.base.mapper.ext;

import io.apirun.api.dto.document.ApiDocumentInfoDTO;
import io.apirun.api.dto.document.ApiDocumentRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtApiDocumentMapper {
    List<ApiDocumentInfoDTO> findApiDocumentSimpleInfoByRequest(@Param("request") ApiDocumentRequest request);
}
