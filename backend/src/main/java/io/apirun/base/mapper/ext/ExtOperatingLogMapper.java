package io.apirun.base.mapper.ext;

import io.apirun.log.vo.OperatingLogDTO;
import io.apirun.log.vo.OperatingLogRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtOperatingLogMapper {

    List<OperatingLogDTO> list(@Param("request") OperatingLogRequest request);

    List<OperatingLogDTO> findBySourceId(@Param("request") OperatingLogRequest request);

}