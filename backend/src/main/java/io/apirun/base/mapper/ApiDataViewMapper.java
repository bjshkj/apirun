package io.apirun.base.mapper;

import io.apirun.api.dto.ApiMonitorSearch;
import io.apirun.api.dto.ApiResponseCodeMonitor;
import io.apirun.api.dto.ApiResponseTimeMonitor;
import io.apirun.base.domain.ApiDataView;

import java.util.List;

public interface ApiDataViewMapper {

    List<ApiMonitorSearch> selectAll();

    List<ApiResponseTimeMonitor> selectResponseTimeByUrl(String url,String startTime,String endTime);

    List<ApiResponseCodeMonitor> selectResponseCodeByUrl(String url,String startTime,String endTime);

    Integer insertListApiData(List<ApiDataView> list);

    Integer deleteByReportId(String reportId);

    String selectReportIdByUrlAndStartTime(String apiUrl,String startTime);
}