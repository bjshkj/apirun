package io.apirun.assemblyline.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.apirun.api.dto.automation.*;
import io.apirun.api.dto.definition.request.MsScenario;
import io.apirun.api.service.ApiAutomationService;
import io.apirun.api.service.ApiScenarioReportService;
import io.apirun.api.service.ApiTestEnvironmentService;
import io.apirun.assemblyline.constants.AsseLineStatus;
import io.apirun.assemblyline.dto.AssemblyLineEnvDTO;
import io.apirun.assemblyline.dto.AssemblyLineScenarioEnvDTO;
import io.apirun.assemblyline.dto.AssemblyLineScenarioTaskDTO;
import io.apirun.assemblyline.dto.JmeterTaskResponseDTO;
import io.apirun.base.domain.*;
import io.apirun.base.mapper.ApiScenarioReportMapper;
import io.apirun.base.mapper.UserMapper;
import io.apirun.base.mapper.ext.ExtApiScenarioMapper;

import io.apirun.commons.constants.*;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.DateUtils;
import io.apirun.commons.utils.LogUtil;
import io.apirun.dto.AssemblyLineScenarioDTO;
import io.apirun.base.mapper.ext.ExtAssemblyLineScenarioMapper;
import io.apirun.i18n.Translator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static io.apirun.commons.utils.IPUtil.getLocalIP;

@Service
public class AssemblylineService {
    @Value("${server.port}")
    private String port;
    @Resource
    ExtAssemblyLineScenarioMapper extAssemblyLineScenarioMapper;
    @Resource
    ExtApiScenarioMapper extApiScenarioMapper;
    @Resource
    ApiTestEnvironmentService apiTestEnvironmentService;
    @Resource
    ApiAutomationService apiAutomationService;
    @Resource
    ApiScenarioReportMapper apiScenarioReportMapper;
    @Resource
    private ApiScenarioReportService apiReportService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private Environment environment;

    final String REPORT_SHOW_URL = "/jmeter?reportId=";
    final String BASE_URL = "";

    //根据类型获取指定信息(场景/用例/case)
    public List<AssemblyLineScenarioDTO> getProjectSpecifiedByType(String project_id, String type) {
        List<AssemblyLineScenarioDTO> scenarioInfo = null;
        if (type.equals(AssemblyLineConstans.SCENARIO.toString())) {
            scenarioInfo = extAssemblyLineScenarioMapper.getAssemblyLineScenarios(project_id);
            LogUtil.info("执行类型:" + AssemblyLineConstans.SCENARIO);
        }
        return scenarioInfo;
    }

    //获取项目的环境列表
    public List<AssemblyLineEnvDTO> getEnvListByProjectId(String project_id) {
        List<ApiTestEnvironmentWithBLOBs> envAllInfo = apiTestEnvironmentService.list(project_id);
        List<AssemblyLineEnvDTO> envList = new ArrayList<>();
        if (envAllInfo.size() != 0) {
            LogUtil.info("获取环境信息：" + envAllInfo);
            for (ApiTestEnvironmentWithBLOBs env : envAllInfo) {
                AssemblyLineEnvDTO envs = new AssemblyLineEnvDTO();
                envs.setEnvId(env.getId());
                envs.setEnvName(env.getName());
                envList.add(0, envs);
            }
        }
        return envList;
    }

    public List<AssemblyLineScenarioDTO> searchInfosByQuery(String project_id, String type, String query) {
        List<AssemblyLineScenarioDTO> search = null;
        if (StringUtils.isNotBlank(type) && StringUtils.equals(type, AssemblyLineConstans.SCENARIO.toString())) {
            LogUtil.info("搜索类型：" + AssemblyLineConstans.SCENARIO);
            if (!StringUtils.isNotBlank(query) || query.length() != 0) {
                LogUtil.info("查询关键字：" + query);
                search = extAssemblyLineScenarioMapper.searchAssemblyLineScenarios(project_id, query);
            }
        }
        return search;
    }

    //流水线场景任务执行
    public String assemblyLineScenarioRun(AssemblyLineScenarioTaskDTO request) {
        //获取场景id，环境id
        List<AssemblyLineScenarioEnvDTO> scenarioEnvList = request.getScenarioEnv();
        for (AssemblyLineScenarioEnvDTO scenario : scenarioEnvList) {
            if (!StringUtils.isNotBlank(scenario.getScenarioId())) {
                return "场景id为空";
            }
            if (!StringUtils.isNotBlank(scenario.getEnvId())) {
                return "环境信息为空";
            }
        }
        String projectId = request.getProjectId();//项目id
        String taskId = UUID.randomUUID().toString();
        String taskName = request.getTaskName();
        String email=null;
        if (StringUtils.isNotBlank(request.getEmail())){
            email=request.getEmail();
        }
        List<String> scenarioIds = scenarioEnvList.stream().map(AssemblyLineScenarioEnvDTO
                ::getScenarioId).collect(Collectors.toList());
        //处理场景ids
        StringBuilder idStr = new StringBuilder();
        scenarioIds.forEach(item -> idStr.append("\"").append(item).append("\"").append(","));
        //获取场景信息
        List<ApiScenarioWithBLOBs> apiScenarios = extApiScenarioMapper.selectByIds(idStr.substring(0, idStr.toString().length() - 1), "\"" + StringUtils.join(scenarioIds, ",") + "\"");
        //对比所选的环境信息与场景原先环境是否相同，不同则进行修改
        List<ApiScenarioWithBLOBs> backApiScenarios = this.handlerEnvironment(projectId, taskId, scenarioEnvList, apiScenarios);
        if (backApiScenarios != null && backApiScenarios.size() == 1 && (backApiScenarios.get(0).getStepTotal() == null || backApiScenarios.get(0).getStepTotal() == 0)) {
            MSException.throwException((backApiScenarios.get(0).getName() + "，" + Translator.get("automation_exec_info")));
        }
        RunScenarioRequest runScenarioRequest = handlerRequest(taskName, scenarioIds);

        if (StringUtils.isEmpty(runScenarioRequest.getProjectId())) {
            runScenarioRequest.setProjectId(projectId);
        }
        if (runScenarioRequest.getConfig() != null && runScenarioRequest.getConfig().getMode().equals(RunModeConstants.SERIAL.toString())) {
            if (StringUtils.equals(runScenarioRequest.getConfig().getReportType(), RunModeConstants.SET_REPORT.toString()) && StringUtils.isNotEmpty(runScenarioRequest.getConfig().getReportName())) {
                runScenarioRequest.setExecuteType(ExecuteType.Completed.name());
            }
        }
        String reportId = runScenarioRequest.getId();
        Map<APIScenarioReportResult, HashTree> map = new LinkedHashMap<>();
        List<String> ids = new ArrayList<>();
        StringBuilder scenarioNames = new StringBuilder();
        assert backApiScenarios != null;
        for (ApiScenarioWithBLOBs item : backApiScenarios) {
            if (item.getStepTotal() == null || item.getStepTotal() == 0) {
                continue;
            }
            APIScenarioReportResult report;
            Map<String, String> planEnvMap = new HashMap<>();
            report = createAssemblyScenarioReport(reportId, item.getId(), item.getName(), runScenarioRequest.getTriggerMode(),
                    runScenarioRequest.getExecuteType(), item.getProjectId(), email, null);
            // 生成报告和HashTree
            HashTree hashTree = null;
            try {
                hashTree = apiAutomationService.generateHashTree(item, reportId, planEnvMap);
            } catch (Exception ex) {
                LogUtil.debug(ex.getMessage());
                // MSException.throwException("解析运行步骤失败！场景名称：" + item.getName());
                return "解析运行步骤失败！场景名称:" + item.getName();
            }
            map.put(report, hashTree);
            ids.add(item.getId());
            scenarioNames.append(item.getName()).append(",");
            reportId = UUID.randomUUID().toString();
        }
        // 生成集成报告
        if (runScenarioRequest.getConfig() != null && runScenarioRequest.getConfig().getMode().equals(RunModeConstants.SERIAL.toString()) && StringUtils.equals(runScenarioRequest.getConfig().getReportType(), RunModeConstants.SET_REPORT.toString()) && StringUtils.isNotEmpty(runScenarioRequest.getConfig().getReportName())) {
            APIScenarioReportResult report = createAssemblyScenarioReport(runScenarioRequest.getConfig().getReportId(), JSON.toJSONString(ids), scenarioNames.deleteCharAt(scenarioNames.toString().length() - 1).toString(), ReportTriggerMode.ASSEMBLY_LINE.name(),
                    ExecuteType.Saved.name(), request.getProjectId(), email, runScenarioRequest.getConfig());
            report.setName(runScenarioRequest.getConfig().getReportName());
            report.setId(taskId);
            try {
                apiScenarioReportMapper.insert(report);  //保存执行的报告
            } catch (Exception ex) {
                LogUtil.error(ex.getMessage());
                return "报告保存出现异常";
            }
        }
        // 开始执行
        if (!map.isEmpty()) {
            try {
                LogUtil.info("流水线Jmeter任务开始执行！");
                apiAutomationService.run(map, runScenarioRequest, taskId);
            } catch (Exception ex) {
                LogUtil.error(ex.getMessage());
                return "流水线Jmeter任务执行异常";
            }
        }
        return taskId;
    }

    //处理场景环境
    public List<ApiScenarioWithBLOBs> handlerEnvironment(String projectId, String taskId, List<AssemblyLineScenarioEnvDTO> scenarioEnvList, List<ApiScenarioWithBLOBs> apiScenarios) {
        List<ApiScenarioWithBLOBs> backApiScenarios = new ArrayList<>();
        for (ApiScenarioWithBLOBs apiScenarioWithBLOBs : apiScenarios) {
            ApiScenarioWithBLOBs apiScenario = handlerScenarioEnvironment(projectId, apiScenarioWithBLOBs, scenarioEnvList);  //检查场景环境
            apiScenario.setReportId(taskId);
            backApiScenarios.add(apiScenario);
        }
        return backApiScenarios;
    }

    private ApiScenarioWithBLOBs handlerScenarioEnvironment(String projectId, ApiScenarioWithBLOBs apiScenarioWithBLOBs, List<AssemblyLineScenarioEnvDTO> scenarioEnvList) {
        String definition = apiScenarioWithBLOBs.getScenarioDefinition();//取出场景中场景定义的部分
        JSONObject json = JSONObject.parseObject(definition);
        MsScenario scenario = JSONObject.parseObject(definition, MsScenario.class);  //使用jsonobject处理
        Map<String, String> envMap = scenario.getEnvironmentMap();  //拿到场景定义里面的环境map
        for (AssemblyLineScenarioEnvDTO env : scenarioEnvList) {
            if (apiScenarioWithBLOBs.getId().equals(env.getScenarioId())) {
                if (!envMap.isEmpty()) {
                    if (!envMap.get(projectId).equals(env.getEnvId())) {
                        envMap.put(projectId, env.getEnvId());
                    } else {
                        return apiScenarioWithBLOBs;
                    }
                } else {
                    envMap.put(projectId, env.getEnvId());
                }
            }
        }
        json.put("environmentMap", envMap);
        scenario.setEnvironmentMap(envMap);
        LogUtil.info("设置environmentMap:" + envMap);
        apiScenarioWithBLOBs.setScenarioDefinition(JSONObject.toJSONString(json));
        return apiScenarioWithBLOBs;
    }

    public RunScenarioRequest handlerRequest(String taskName, List<String> scenarioIds) {
        RunScenarioRequest request = new RunScenarioRequest();
        request.setTriggerMode(ReportTriggerMode.ASSEMBLY_LINE.name());  //设置触发方式为流水线触发
        request.setRunMode(ApiRunMode.SCENARIO.name());
        request.setIds(scenarioIds);
        request.setId(UUID.randomUUID().toString());
        request.setExecuteType(ExecuteType.Completed.name());
        RunModeConfig config = new RunModeConfig();
        config.setReportName(taskName);
        config.setMode(RunModeConstants.SERIAL.toString());
        config.setReportType(RunModeConstants.SET_REPORT.toString());
        config.setOnSampleError(false);
        request.setConfig(config);
        return request;
    }

    //创建流水线场景执行报告
    public APIScenarioReportResult createAssemblyScenarioReport(String id, String scenarioId, String scenarioName, String triggerMode, String execType, String projectId, String email, RunModeConfig config) {
        APIScenarioReportResult report = new APIScenarioReportResult();
        if (triggerMode.equals(ApiRunMode.SCENARIO.name()) || triggerMode.equals(ApiRunMode.DEFINITION.name())) {
            triggerMode = ReportTriggerMode.ASSEMBLY_LINE.name();  //设置为流水线触发
        }
        report.setId(id);
        report.setTestId(id);
        report.setName(scenarioName);
        report.setUpdateTime(System.currentTimeMillis());
        report.setCreateTime(System.currentTimeMillis());
        if (config != null && config.getMode().equals(RunModeConstants.SERIAL.toString())) {
            report.setCreateTime(System.currentTimeMillis() + 2000);
            report.setUpdateTime(System.currentTimeMillis() + 2000);
        }
        report.setStatus(APITestStatus.Running.name());
        try {
            if (StringUtils.isNotEmpty(email)) {
                User user = userMapper.selectByEmail(email);
                if (user != null){
                    report.setUserId(user.getId());
                    report.setCreateUser(user.getName());
                }else {
                    report.setUserId(email.substring(0,email.indexOf("@")));
                    report.setCreateUser(email);
                }
            }
        }catch (Exception e){
            LogUtil.info(e);
        }
        report.setTriggerMode(triggerMode);
        report.setExecuteType(execType);
        report.setProjectId(projectId);
        report.setScenarioName(scenarioName);
        report.setScenarioId(scenarioId);
        return report;
    }

    public JmeterTaskResponseDTO getJmeterTaskResult(String taskId) {
        APIScenarioReportResult apiScenarioReportResult = apiReportService.get(taskId);
        JmeterTaskResponseDTO jmeterTaskResponse = new JmeterTaskResponseDTO();
        try {
            if (apiScenarioReportResult.getStatus().equals("Running")) {
                jmeterTaskResponse.setStatus(AsseLineStatus.EXECUTING);
            } else {
                String reportUrl = getReportUrl(taskId);
                jmeterTaskResponse.setUrl(reportUrl);
                String content = apiScenarioReportResult.getContent();
                JSONObject contentJson = JSON.parseObject(content);
                jmeterTaskResponse.setScenarioTotalNum((Integer) contentJson.get("scenarioTotal"));
                jmeterTaskResponse.setScenarioSuccessNum((Integer) contentJson.get("scenarioSuccess"));
                jmeterTaskResponse.setScenarioFailedNum((Integer) contentJson.get("scenarioError"));
                jmeterTaskResponse.setScenarioRequestTotalNum((Integer) contentJson.get("total"));
                jmeterTaskResponse.setScenarioRequestSuccessNum((Integer) contentJson.get("success"));
                jmeterTaskResponse.setScenarioRequestFailedNum((Integer) contentJson.get("error"));
                if (StringUtils.equals(apiScenarioReportResult.getStatus(), "Success")) {
                    jmeterTaskResponse.setStatus(AsseLineStatus.SUCCESS);
                }
                if (StringUtils.equals(apiScenarioReportResult.getStatus(), "Error")) {
                    if ((int) contentJson.get("success") > 0 && (int) contentJson.get("success") < (int) contentJson.get("total")) {
                        jmeterTaskResponse.setStatus(AsseLineStatus.PARTIAL_SUCCESS);
                    }
                    if ((int) contentJson.get("error") > 0 && (int) contentJson.get("error") == (int) contentJson.get("total")) {
                        jmeterTaskResponse.setStatus(AsseLineStatus.FAILURE);
                    }
                }
                apiScenarioReportResult.setName(apiScenarioReportResult.getName() + "-" + DateUtils.getTimeStr(System.currentTimeMillis()));
                apiScenarioReportResult.setTestName(apiScenarioReportResult.getName());
                apiScenarioReportMapper.updateByPrimaryKeySelective(apiScenarioReportResult);
                LogUtil.info("流水线Jmeter任务完成，报告:{}",apiScenarioReportResult.getName());
            }
        } catch (Exception e) {
            LogUtil.info("流水线Jmeter 任务执行异常:" + e);
            jmeterTaskResponse.setStatus(AsseLineStatus.FAILURE);
        }
        LogUtil.info("流水线Jmeter任务状态：" + jmeterTaskResponse.getStatus());
        return jmeterTaskResponse;
    }

    public String getReportUrl(String taskId) {
        String ip = getLocalIP();
        String reportUrl = "";
        LogUtil.info("IP：{},端口：{}", ip, port);
        //获取当前环境是生产还是测试
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "test".equals(profile)) {
                reportUrl = String.format("http://%s:%s%s%s", ip, port, REPORT_SHOW_URL, taskId);
                LogUtil.info("流水线Jmeter任务执行报告URL:" + reportUrl);
                break;
            } else if ("prod".equals(profile)) {
                reportUrl = String.format("%s%s%s", BASE_URL, REPORT_SHOW_URL, taskId);
                LogUtil.info("流水线Jmeter任务执行报告URL:" + reportUrl);
                break;
            }
        }
        return reportUrl;
    }
}
