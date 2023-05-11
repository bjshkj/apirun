package io.apirun.base.mapper.ext;

import io.apirun.base.domain.LoadTestReportWithBLOBs;
import io.apirun.dto.DashboardTestDTO;
import io.apirun.dto.ReportDTO;
import io.apirun.performance.controller.request.ReportRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtLoadTestReportMapper {

    List<ReportDTO> getReportList(@Param("reportRequest") ReportRequest request);

    ReportDTO getReportTestAndProInfo(@Param("id") String id);

    List<DashboardTestDTO> selectDashboardTests(@Param("workspaceId") String workspaceId, @Param("startTimestamp") long startTimestamp);

    List<String> selectResourceId(@Param("reportId") String reportId);

    void updateJmxContentIfAbsent(LoadTestReportWithBLOBs record);
}
