package io.apirun.statistics.controller;

import io.apirun.statistics.dto.CaseCountRequest;
import io.apirun.statistics.dto.CasesTotalDTO;
import io.apirun.statistics.dto.ChartDataDTO;
import io.apirun.statistics.dto.ProjectTypeDTO;
import io.apirun.statistics.service.ReportStatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/report/statistics")
public class ReportStatisticsController {

    @Resource
    private ReportStatisticsService reportStatisticsService;

    @PostMapping(value ="/cases/total")
    public CasesTotalDTO getCasesTotal(@RequestBody CaseCountRequest request){
        if (request.getWorkspaceId().equals("")){
            request.setWorkspaceId(null);
        }
        return reportStatisticsService.getCasesTotal(request);
    }

    @PostMapping(value = "/count/projectType")
    public List<ProjectTypeDTO> countProjectType(@RequestBody CaseCountRequest request){
        if (request.getWorkspaceId().equals("")){
            request.setWorkspaceId(null);
        }
        return reportStatisticsService.countProjectType(request);
    }

    @PostMapping(value = "/getChartData")
    public ChartDataDTO getChartData(@RequestBody CaseCountRequest request){
        return reportStatisticsService.getChartData(request);
    }
}
