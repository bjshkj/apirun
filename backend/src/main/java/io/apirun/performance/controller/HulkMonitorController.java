package io.apirun.performance.controller;

import io.apirun.base.domain.LoadTestMonitorDataWithBLOBs;
import io.apirun.performance.service.HulkMonitorService;
import io.apirun.performance.service.LoadTestMonitorDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/hulk")
public class HulkMonitorController {

    @Resource
    private HulkMonitorService hulkMonitorService;

    @GetMapping("/monitor/{id}")
    public List<LoadTestMonitorDataWithBLOBs> monitorData(@PathVariable("id") String reportId){
        return hulkMonitorService.queryMetric(reportId);
    }

    @GetMapping("/query/resource/{id}")
    public List<String> queryReportResource(@PathVariable("id") String reportId) {
        return hulkMonitorService.queryReportResource(reportId);
    }

    @GetMapping("/monitorData/{id}")
    public List<LoadTestMonitorDataWithBLOBs> getMonitorData(@PathVariable("id") String reportId){
        return hulkMonitorService.getMonitorData(reportId);
    }
}
