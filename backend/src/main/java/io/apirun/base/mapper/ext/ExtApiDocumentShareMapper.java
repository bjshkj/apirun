package io.apirun.base.mapper.ext;

import io.apirun.base.domain.ApiDocumentShare;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtApiDocumentShareMapper {
    List<ApiDocumentShare> selectByShareTypeAndShareApiIdWithBLOBs(@Param("shareType") String shareType, @Param("shareApiId") String shareApiIdString);
}
