package io.apirun.base.mapper.ext;

import io.apirun.dto.StatisticsUserRankingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtWorkspaceUserActivityMapper {
    List<StatisticsUserRankingDTO> getWorkspaceUserActivity(@Param("workspace_id") String workspace_id, @Param("begin_time") String begin_time, @Param("end_time") String end_time);

}
