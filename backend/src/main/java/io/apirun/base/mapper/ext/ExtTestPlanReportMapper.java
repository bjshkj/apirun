package io.apirun.base.mapper.ext;

import io.apirun.track.dto.TestPlanReportDTO;
import io.apirun.track.request.report.QueryTestPlanReportRequest;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author song.tianyang
 * @Date 2021/1/8 4:58 下午
 * @Description
 */
public interface ExtTestPlanReportMapper {
    public List<TestPlanReportDTO> list(@Param("request")QueryTestPlanReportRequest request);
}
