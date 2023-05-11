package io.apirun.base.mapper.ext;

import io.apirun.api.dto.QueryAPIReportRequest;
import io.apirun.api.dto.automation.APIScenarioReportResult;
import io.apirun.api.dto.datacount.ApiDataCountResult;
import io.apirun.base.domain.ApiScenarioReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtApiScenarioReportMapper {
    List<APIScenarioReportResult> list(@Param("request") QueryAPIReportRequest request);

    APIScenarioReportResult get(@Param("reportId") String reportId);

    long countByProjectID(String projectId);

    long countByProjectIdAndCreateInThisWeek(@Param("projectId") String projectId, @Param("firstDayTimestamp") long firstDayTimestamp, @Param("lastDayTimestamp") long lastDayTimestamp);

    long countByProjectIdAndCreateAndByScheduleInThisWeek(@Param("projectId") String projectId, @Param("firstDayTimestamp") long firstDayTimestamp, @Param("lastDayTimestamp") long lastDayTimestamp);

    List<ApiDataCountResult> countByProjectIdGroupByExecuteResult(String projectId);

    List<ApiScenarioReport> selectLastReportByIds(@Param("scenarioIdList") List<String> ids);

    ApiScenarioReport selectPreviousReportByScenarioId(@Param("scenarioId") String scenarioId, @Param("nowId") String nowId);

    List<String> idList(@Param("request") QueryAPIReportRequest request);
}