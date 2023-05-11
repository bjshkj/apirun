package io.apirun.base.mapper.ext;

import io.apirun.dto.StatisticsProjectRankingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtProjectsActivityRankingMapper {
    List<StatisticsProjectRankingDTO> projectsActivityRanking(@Param("begin_time") String begin_time, @Param("end_time") String end_time, @Param("query_type") String query_type);

}
