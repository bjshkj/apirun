package io.apirun.base.mapper;

import io.apirun.statistics.dto.CaseCountRequest;
import io.apirun.statistics.dto.EveryDayDataDTO;
import io.apirun.statistics.dto.ProjectTypeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportStatisticsMapper {

    long countApi(CaseCountRequest request);
    long countCase(CaseCountRequest request);
    long countScene(CaseCountRequest request);
    long countPerformance(CaseCountRequest request);
    long countHttpRunner(CaseCountRequest request);
    //统计一段时间内增加的用例（接口、接口用例、场景用例、性能用例、httprunner用例）
    List<ProjectTypeDTO> countProjectTypeByWorkerSpacesIDInTimeSpace(CaseCountRequest request);
    int countProjectTypeByWorkerSpacesIDNewNum(CaseCountRequest request);
    List<EveryDayDataDTO> countApiInTimeSpace(CaseCountRequest request);
    List<EveryDayDataDTO> countCaseInTimeSpace(CaseCountRequest request);
    List<EveryDayDataDTO> countSceneInTimeSpace(CaseCountRequest request);
    List<EveryDayDataDTO> countPerformanceInTimeSpace(CaseCountRequest request);
    List<EveryDayDataDTO> countHttpRunnerInTimeSpace(CaseCountRequest request);
    //统计一段时间内修改、删除的用例（接口、接口用例、场景用例、性能用例）
    List<EveryDayDataDTO> countCasesInTimeSpace(CaseCountRequest request);
}
