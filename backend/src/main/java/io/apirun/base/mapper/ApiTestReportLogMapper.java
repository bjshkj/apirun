package io.apirun.base.mapper;


import io.apirun.base.domain.ApiTestReportLog;
import io.apirun.base.domain.LoadTestReportLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiTestReportLogMapper {

    List<ApiTestReportLog> selectByExampleWithBLOBs(LoadTestReportLogExample example);

    long countByExample(LoadTestReportLogExample example);

    void insert(ApiTestReportLog record);

    void deleteByPrimaryKey(String reportId);

    List<ApiTestReportLog> selectByPrimaryKey(String reportId);
}
