package io.apirun.assemblyline.controller;

import io.apirun.assemblyline.dto.AssemblyLineEnvDTO;
import io.apirun.assemblyline.dto.AssemblyLineScenarioTaskDTO;
import io.apirun.assemblyline.dto.JmeterTaskResponseDTO;
import io.apirun.dto.AssemblyLineScenarioDTO;
import io.apirun.assemblyline.service.AssemblylineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/assemblyline/jmeter")
@RestController
public class JmeterAssemblyLineController {
    @Resource
    AssemblylineService assemblylineService;

    //获取指定jmeter项目下的信息，根据type确定场景/用例/接口
    @GetMapping("/getProjectRelatedInfos")
    public List<AssemblyLineScenarioDTO> getProjectInfosByProjectId(@RequestParam("projectId") String projectId, @RequestParam("type") String type) {
        if (!StringUtils.isNotBlank(type)) {
            return null;
        }
        return assemblylineService.getProjectSpecifiedByType(projectId, type);
    }

    //根据project_id获取指定项目的所有环境--环境列表展示
    @GetMapping("/getProjectAllEnvs")
    public List<AssemblyLineEnvDTO> getProjectEnvList(@RequestParam("projectId") String projectId) {
        if (!StringUtils.isNotBlank(projectId)) {
            return null;
        }
        return assemblylineService.getEnvListByProjectId(projectId);
    }

    // 关键词搜索项目下所有的场景/case/接口
    @GetMapping("/searchProjectInfosByQuery")
    public List<AssemblyLineScenarioDTO> searchProjectInfosByQuery(@RequestParam("projectId") String project_id, @RequestParam("type") String type, @RequestParam("query") String query) {
        return assemblylineService.searchInfosByQuery(project_id, type, query);
    }

    // 流水线场景任务执行
    @PostMapping(value = "/scenario/run/batch")
    public String runAssemblyLineScenarios(@RequestBody AssemblyLineScenarioTaskDTO request) {
        return assemblylineService.assemblyLineScenarioRun(request);
    }

    @GetMapping(value = "/getTaskStatus/{taskId}")
    public JmeterTaskResponseDTO getJmeterTaskStatus(@PathVariable String taskId){
        return assemblylineService.getJmeterTaskResult(taskId);
    }
}
