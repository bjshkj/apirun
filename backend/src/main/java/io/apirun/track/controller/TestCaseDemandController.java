package io.apirun.track.controller;

import io.apirun.track.dto.DemandDTO;
import io.apirun.track.service.DemandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("demand")
@RestController
public class TestCaseDemandController {
    @Resource
    private DemandService DemandService;

    @GetMapping("/list/{projectId}")
    public List<DemandDTO> getDemandList(@PathVariable String projectId) {
        return DemandService.getDemandList(projectId);
    }
}
