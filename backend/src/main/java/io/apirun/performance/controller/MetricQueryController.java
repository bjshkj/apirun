package io.apirun.performance.controller;

import io.apirun.performance.dto.MetricData;
import io.apirun.performance.service.MetricQueryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/metric")
public class MetricQueryController {

    @Resource
    private MetricQueryService metricService;

    @GetMapping("/query/{id}")
    public List<MetricData> queryMetric(@PathVariable("id") String reportId) {
        return metricService.queryMetric(reportId);
    }

    @GetMapping("/query/resource/{id}")
    public List<String> queryReportResource(@PathVariable("id") String reportId) {
        return metricService.queryReportResource(reportId);
    }
}
