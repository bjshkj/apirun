package io.apirun.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apirun.api.dto.*;
import io.apirun.api.dto.automation.*;
import io.apirun.api.dto.automation.parse.ScenarioImport;
import io.apirun.api.dto.automation.parse.ScenarioImportParserFactory;
import io.apirun.api.dto.datacount.ApiDataCountResult;
import io.apirun.api.dto.definition.RunDefinitionRequest;
import io.apirun.api.dto.definition.request.*;
import io.apirun.api.dto.definition.request.sampler.MsHTTPSamplerProxy;
import io.apirun.api.dto.definition.request.unknown.MsJmeterElement;
import io.apirun.api.dto.definition.request.variable.ScenarioVariable;
import io.apirun.api.dto.scenario.environment.EnvironmentConfig;
import io.apirun.api.jmeter.JMeterService;
import io.apirun.api.parse.ApiImportParser;
import io.apirun.api.service.task.ParallelScenarioExecTask;
import io.apirun.api.service.task.SerialScenarioExecTask;
import io.apirun.base.domain.*;
import io.apirun.base.mapper.*;
import io.apirun.base.mapper.ext.*;
import io.apirun.commons.constants.*;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.*;
import io.apirun.controller.request.ScheduleRequest;
import io.apirun.dto.BaseSystemConfigDTO;
import io.apirun.i18n.Translator;
import io.apirun.job.sechedule.ApiScenarioTestJob;
import io.apirun.job.sechedule.SwaggerUrlImportJob;
import io.apirun.job.sechedule.TestPlanTestJob;
import io.apirun.log.utils.ReflexObjectUtil;
import io.apirun.log.vo.DetailColumn;
import io.apirun.log.vo.OperatingLogDetails;
import io.apirun.log.vo.api.AutomationReference;
import io.apirun.service.ScheduleService;
import io.apirun.service.SystemParameterService;
import io.apirun.track.dto.TestPlanDTO;
import io.apirun.track.request.testcase.ApiCaseRelevanceRequest;
import io.apirun.track.request.testcase.QueryTestPlanRequest;
import io.apirun.track.request.testplan.FileOperationRequest;
import io.apirun.track.service.TestPlanScenarioCaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static io.apirun.commons.constants.ApiFixCryptoConstants.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApiAutomationService {
    @Resource
    ApiScenarioModuleMapper apiScenarioModuleMapper;
    @Resource
    private ExtScheduleMapper extScheduleMapper;
    @Resource
    private ApiScenarioMapper apiScenarioMapper;
    @Resource
    private APITestService apiTestService;
    @Resource
    private ExtApiScenarioMapper extApiScenarioMapper;
    @Resource
    private TestPlanApiScenarioMapper testPlanApiScenarioMapper;
    @Resource
    private TestCaseReviewScenarioMapper testCaseReviewScenarioMapper;
    @Resource
    private JMeterService jMeterService;
    @Resource
    private ApiTestEnvironmentService environmentService;
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private ApiScenarioReportService apiReportService;
    @Resource
    private ExtTestPlanMapper extTestPlanMapper;
    @Resource
    SqlSessionFactory sqlSessionFactory;
    @Resource
    private ApiScenarioReportMapper apiScenarioReportMapper;
    @Resource
    @Lazy
    private TestPlanScenarioCaseService testPlanScenarioCaseService;
    @Resource
    private EsbApiParamService esbApiParamService;
    @Resource
    private ApiTestCaseService apiTestCaseService;
    @Resource
    private ApiDefinitionService apiDefinitionService;
    @Resource
    private ApiTestEnvironmentMapper apiTestEnvironmentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private ApiScenarioReportService apiScenarioReportService;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private TestPlanMapper testPlanMapper;

    private static int cpuCount = Runtime.getRuntime().availableProcessors();

    public ApiScenarioWithBLOBs getDto(String id) {
        return apiScenarioMapper.selectByPrimaryKey(id);
    }

    public ApiTestEnvironment get(String id) {
        return apiTestEnvironmentMapper.selectByPrimaryKey(id);
    }

    public User getUser(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public List<ApiScenarioDTO> list(ApiScenarioRequest request) {
        request = this.initRequest(request, true, true);
        List<ApiScenarioDTO> list = extApiScenarioMapper.list(request);
        return list;
    }

    public List<ApiScenarioWithBLOBs> listAll(ApiScenarioBatchRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));
        List<ApiScenarioWithBLOBs> list = extApiScenarioMapper.selectIds(request.getIds());
        return list;
    }

    public List<String> idAll(ApiScenarioBatchRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));
        return request.getIds();
    }

    public List<ApiScenarioDTO> listReview(ApiScenarioRequest request) {
        request = this.initRequest(request, true, true);
        List<ApiScenarioDTO> list = extApiScenarioMapper.listReview(request);
        return list;
    }

    private void setApiScenarioProjectIds(ApiScenarioDTO data) {
        // 如果场景步骤涉及多项目，则把涉及到的项目ID保存在projectIds属性
        List<String> idList = new ArrayList<>();
        String definition = data.getScenarioDefinition();
        if (StringUtils.isNotBlank(definition)) {
            RunDefinitionRequest d = JSON.parseObject(definition, RunDefinitionRequest.class);

            if (d != null) {
                Map<String, String> map = d.getEnvironmentMap();
                if (map != null) {
                    if (map.isEmpty()) {
                        try {
                            List<String> ids = (List<String>) JSONPath.read(definition, "$..projectId");
                            if (CollectionUtils.isNotEmpty(ids)) {
                                idList.addAll(new HashSet<>(ids));
                            }
                        } catch (Exception e) {
                            LogUtil.error("JSONPath.read projectId fail.");
                        }
                    } else {
                        Set<String> set = d.getEnvironmentMap().keySet();
                        idList = new ArrayList<>(set);
                    }
                } else {
                    // 兼容历史数据，无EnvironmentMap直接赋值场景所属项目
                    idList.add(data.getProjectId());
                }
            }

        }
        data.setProjectIds(idList);
    }

    /**
     * 初始化部分参数
     *
     * @param request
     * @param setDefultOrders
     * @param checkThisWeekData
     * @return
     */
    private ApiScenarioRequest initRequest(ApiScenarioRequest request, boolean setDefultOrders, boolean checkThisWeekData) {
        if (setDefultOrders) {
            request.setOrders(ServiceUtils.getDefaultOrder(request.getOrders()));
        }
        if (StringUtils.isNotEmpty(request.getExecuteStatus())) {
            Map<String, List<String>> statusFilter = new HashMap<>();
            List<String> list = new ArrayList<>();
            list.add("Prepare");
            list.add("Underway");
            list.add("Completed");
            statusFilter.put("status", list);
            request.setFilters(statusFilter);
        }
        if (checkThisWeekData) {
            if (request.isSelectThisWeedData()) {
                Map<String, Date> weekFirstTimeAndLastTime = DateUtils.getWeedFirstTimeAndLastTime(new Date());
                Date weekFirstTime = weekFirstTimeAndLastTime.get("firstTime");
                if (weekFirstTime != null) {
                    request.setCreateTime(weekFirstTime.getTime());
                }
            }
        }
        return request;
    }

    public void removeToGcByIds(List<String> nodeIds) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andApiScenarioModuleIdIn(nodeIds);
        extApiScenarioMapper.removeToGcByExample(example);
    }

    public ApiScenario create(SaveApiScenarioRequest request, List<MultipartFile> bodyFiles, List<MultipartFile> scenarioFiles) {
        request.setId(UUID.randomUUID().toString());
        checkNameExist(request);
        checkScenarioNum(request);
        final ApiScenarioWithBLOBs scenario = buildSaveScenario(request);

        scenario.setCreateTime(System.currentTimeMillis());
        scenario.setNum(getNextNum(request.getProjectId()));

        //检查场景的请求步骤。如果含有ESB请求步骤的话，要做参数计算处理。
        esbApiParamService.checkScenarioRequests(request);

        apiScenarioMapper.insert(scenario);

        uploadFiles(request, bodyFiles, scenarioFiles);

        return scenario;
    }

    private void uploadFiles(SaveApiScenarioRequest request, List<MultipartFile> bodyFiles, List<MultipartFile> scenarioFiles) {
        FileUtils.createBodyFiles(request.getScenarioFileIds(), scenarioFiles);
        List<String> bodyFileRequestIds = request.getBodyFileRequestIds();
        if (CollectionUtils.isNotEmpty(bodyFileRequestIds)) {
            bodyFileRequestIds.forEach(requestId -> {
                FileUtils.createBodyFiles(requestId, bodyFiles);
            });
        }
    }

    private void uploadBodyFiles(List<String> bodyFileRequestIds, List<MultipartFile> bodyFiles) {
        if (CollectionUtils.isNotEmpty(bodyFileRequestIds)) {
            bodyFileRequestIds.forEach(requestId -> {
                FileUtils.createBodyFiles(requestId, bodyFiles);
            });
        }
    }

    private void checkScenarioNum(SaveApiScenarioRequest request) {
        if (StringUtils.isNotBlank(request.getCustomNum())) {
            String projectId = request.getProjectId();
            Project project = projectMapper.selectByPrimaryKey(projectId);
            if (project != null) {
                Boolean customNum = project.getScenarioCustomNum();
                // 未开启自定义ID
                if (!customNum) {
                    request.setCustomNum(null);
                } else {
                    checkCustomNumExist(request);
                }
            } else {
                MSException.throwException("add scenario fail, project is not find.");
            }
        }
    }

    private void checkCustomNumExist(SaveApiScenarioRequest request) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andCustomNumEqualTo(request.getCustomNum())
                .andIdNotEqualTo(request.getId());
        List<ApiScenario> list = apiScenarioMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            MSException.throwException("自定义ID已存在！");
        }
    }

    private int getNextNum(String projectId) {
        ApiScenario apiScenario = extApiScenarioMapper.getNextNum(projectId);
        if (apiScenario == null) {
            return 100001;
        } else {
            return Optional.of(apiScenario.getNum() + 1).orElse(100001);
        }
    }

    public void update(SaveApiScenarioRequest request, List<MultipartFile> bodyFiles, List<MultipartFile> scenarioFiles) {
        checkNameExist(request);
        checkScenarioNum(request);

        //检查场景的请求步骤。如果含有ESB请求步骤的话，要做参数计算处理。
        esbApiParamService.checkScenarioRequests(request);

        final ApiScenarioWithBLOBs scenario = buildSaveScenario(request);
        deleteUpdateBodyFile(scenario);
        apiScenarioMapper.updateByPrimaryKeySelective(scenario);
        extScheduleMapper.updateNameByResourceID(request.getId(), request.getName());//  修改场景name，同步到修改首页定时任务
        uploadFiles(request, bodyFiles, scenarioFiles);
    }

    /**
     * 更新时如果有删除自定义请求，则删除对应body文件
     *
     * @param scenario
     */
    public void deleteUpdateBodyFile(ApiScenarioWithBLOBs scenario) {
        ApiScenarioWithBLOBs oldScenario = apiScenarioMapper.selectByPrimaryKey(scenario.getId());
        Set<String> newRequestIds = getRequestIds(scenario.getScenarioDefinition());
        MsTestElement msTestElement = parseScenarioDefinition(oldScenario.getScenarioDefinition());
        List<MsHTTPSamplerProxy> oldRequests = MsHTTPSamplerProxy.findHttpSampleFromHashTree(msTestElement);
        oldRequests.forEach(item -> {
            if (item.isCustomizeReq() && !newRequestIds.contains(item.getId())) {
                FileUtils.deleteBodyFiles(item.getId());
            }
        });
    }

    public MsScenario parseScenarioDefinition(String scenarioDefinition) {
        MsScenario scenario = JSONObject.parseObject(scenarioDefinition, MsScenario.class);
        parse(scenarioDefinition, scenario);
        return scenario;
    }

    public Set<String> getRequestIds(String scenarioDefinition) {
        MsScenario msScenario = parseScenarioDefinition(scenarioDefinition);
        List<MsHTTPSamplerProxy> httpSampleFromHashTree = MsHTTPSamplerProxy.findHttpSampleFromHashTree(msScenario);
        return httpSampleFromHashTree.stream()
                .map(MsHTTPSamplerProxy::getId).collect(Collectors.toSet());
    }

    public ApiScenarioWithBLOBs buildSaveScenario(SaveApiScenarioRequest request) {
        ApiScenarioWithBLOBs scenario = new ApiScenarioWithBLOBs();
        scenario.setId(request.getId());
        scenario.setName(request.getName());
        scenario.setProjectId(request.getProjectId());
        scenario.setCustomNum(request.getCustomNum());
        if (StringUtils.equals(request.getTags(), "[]")) {
            scenario.setTags("");
        } else {
            scenario.setTags(request.getTags());
        }
        scenario.setApiScenarioModuleId(request.getApiScenarioModuleId());
        scenario.setModulePath(request.getModulePath());
        scenario.setLevel(request.getLevel());
        scenario.setFollowPeople(request.getFollowPeople());
        scenario.setPrincipal(request.getPrincipal());
        scenario.setStepTotal(request.getStepTotal());
        scenario.setUpdateTime(System.currentTimeMillis());
        scenario.setDescription(request.getDescription());
        scenario.setCreateUser(SessionUtils.getUserId());

        scenario.setScenarioDefinition(JSON.toJSONString(request.getScenarioDefinition()));
        if (StringUtils.isNotEmpty(request.getStatus())) {
            scenario.setStatus(request.getStatus());
        } else {
            scenario.setStatus(ScenarioStatus.Underway.name());
        }
        if (StringUtils.isNotEmpty(request.getUserId())) {
            scenario.setUserId(request.getUserId());
        } else {
            scenario.setUserId(SessionUtils.getUserId());
        }

        if (StringUtils.isEmpty(request.getApiScenarioModuleId()) || "default-module".equals(request.getApiScenarioModuleId())) {
            ApiScenarioModuleExample example = new ApiScenarioModuleExample();
            example.createCriteria().andProjectIdEqualTo(request.getProjectId()).andNameEqualTo("默认模块");
            List<ApiScenarioModule> modules = apiScenarioModuleMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(modules)) {
                scenario.setApiScenarioModuleId(modules.get(0).getId());
                scenario.setModulePath(modules.get(0).getName());
            }
        }
        return scenario;
    }

    public void delete(String id) {
        //及连删除外键表
        this.preDelete(id);
        testPlanScenarioCaseService.deleteByScenarioId(id);
        apiScenarioMapper.deleteByPrimaryKey(id);
    }

    public void preDelete(String scenarioId) {
        List<String> ids = new ArrayList<>();
        ids.add(scenarioId);
        deleteApiScenarioReport(ids);

        scheduleService.deleteScheduleAndJobByResourceId(scenarioId, ScheduleGroup.API_SCENARIO_TEST.name());
        TestPlanApiScenarioExample example = new TestPlanApiScenarioExample();
        example.createCriteria().andApiScenarioIdEqualTo(scenarioId);
        List<TestPlanApiScenario> testPlanApiScenarioList = testPlanApiScenarioMapper.selectByExample(example);

        List<String> idList = new ArrayList<>(testPlanApiScenarioList.size());
        for (TestPlanApiScenario api :
                testPlanApiScenarioList) {
            idList.add(api.getId());
        }
        example = new TestPlanApiScenarioExample();

        if (!idList.isEmpty()) {
            example.createCriteria().andIdIn(idList);
            testPlanApiScenarioMapper.deleteByExample(example);
        }

        deleteBodyFileByScenarioId(scenarioId);
    }

    public void deleteBodyFileByScenarioId(String scenarioId) {
        ApiScenarioWithBLOBs apiScenarioWithBLOBs = apiScenarioMapper.selectByPrimaryKey(scenarioId);
        String scenarioDefinition = apiScenarioWithBLOBs.getScenarioDefinition();
        deleteBodyFile(scenarioDefinition);
    }

    public void deleteBodyFileByScenarioIds(List<String> ids) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andIdIn(ids);
        List<ApiScenarioWithBLOBs> apiScenarios = apiScenarioMapper.selectByExampleWithBLOBs(example);
        apiScenarios.forEach((item) -> {
            deleteBodyFile(item.getScenarioDefinition());
        });
    }

    public void deleteBodyFile(String scenarioDefinition) {
        MsTestElement msTestElement = parseScenarioDefinition(scenarioDefinition);
        List<MsHTTPSamplerProxy> httpSampleFromHashTree = MsHTTPSamplerProxy.findHttpSampleFromHashTree(msTestElement);
        httpSampleFromHashTree.forEach((httpSamplerProxy) -> {
            if (httpSamplerProxy.isCustomizeReq()) {
                FileUtils.deleteBodyFiles(httpSamplerProxy.getId());
            }
        });
    }

    private void deleteApiScenarioReport(List<String> scenarioIds) {
        if (scenarioIds == null || scenarioIds.isEmpty()) {
            return;
        }
        ApiScenarioReportExample scenarioReportExample = new ApiScenarioReportExample();
        scenarioReportExample.createCriteria().andScenarioIdIn(scenarioIds);
        List<ApiScenarioReport> list = apiScenarioReportMapper.selectByExample(scenarioReportExample);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> ids = list.stream().map(ApiScenarioReport::getId).collect(Collectors.toList());
            APIReportBatchRequest reportRequest = new APIReportBatchRequest();
            reportRequest.setIds(ids);
            apiReportService.deleteAPIReportBatch(reportRequest);
        }
    }

    public void preDeleteBatch(List<String> scenarioIds) {
        deleteApiScenarioReport(scenarioIds);

        List<String> testPlanApiScenarioIdList = new ArrayList<>();
        for (String id : scenarioIds) {
            TestPlanApiScenarioExample example = new TestPlanApiScenarioExample();
            example.createCriteria().andApiScenarioIdEqualTo(id);
            List<TestPlanApiScenario> testPlanApiScenarioList = testPlanApiScenarioMapper.selectByExample(example);
            for (TestPlanApiScenario api : testPlanApiScenarioList) {
                if (!testPlanApiScenarioIdList.contains(api.getId())) {
                    testPlanApiScenarioIdList.add(api.getId());
                }
            }

            scheduleService.deleteByResourceId(id);
        }
        if (!testPlanApiScenarioIdList.isEmpty()) {
            TestPlanApiScenarioExample example = new TestPlanApiScenarioExample();
            example.createCriteria().andIdIn(testPlanApiScenarioIdList);
            testPlanApiScenarioMapper.deleteByExample(example);
        }
        deleteBodyFileByScenarioIds(scenarioIds);
    }

    public void deleteBatch(List<String> ids) {
        // 删除外键表
        preDeleteBatch(ids);
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andIdIn(ids);
        apiScenarioMapper.deleteByExample(example);
    }

    public void removeToGc(List<String> apiIds) {
        extApiScenarioMapper.removeToGc(apiIds);
        //将这些场景的定时任务删除掉
        for (String id : apiIds) {
            scheduleService.deleteScheduleAndJobByResourceId(id, ScheduleGroup.API_SCENARIO_TEST.name());
        }
    }

    public void reduction(List<String> ids) {
        extApiScenarioMapper.reduction(ids);
    }

    private void checkNameExist(SaveApiScenarioRequest request) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andNameEqualTo(request.getName()).andProjectIdEqualTo(request.getProjectId()).andStatusNotEqualTo("Trash").andIdNotEqualTo(request.getId());
        if (apiScenarioMapper.countByExample(example) > 0) {
            MSException.throwException(Translator.get("automation_name_already_exists"));
        }
    }

    public ApiScenarioWithBLOBs getApiScenario(String id) {
        return apiScenarioMapper.selectByPrimaryKey(id);
    }

    public LinkedList<MsTestElement> getScenarioHashTree(String definition) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONObject element = JSON.parseObject(definition);
        try {
            return objectMapper.readValue(element.getString("hashTree"), new TypeReference<LinkedList<MsTestElement>>() {
            });
        } catch (JsonProcessingException e) {
            LogUtil.error(e.getMessage(), e);
        }
        return new LinkedList<>();
    }

    public ScenarioEnv getApiScenarioEnv(String definition) {
        ObjectMapper mapper = new ObjectMapper();
        ScenarioEnv env = new ScenarioEnv();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<MsTestElement> hashTree = getScenarioHashTree(definition);
        for (int i = 0; i < hashTree.size(); i++) {
            MsTestElement tr = hashTree.get(i);
            String referenced = tr.getReferenced();
            if (StringUtils.equals(MsTestElementConstants.REF.name(), referenced)) {
                if (StringUtils.equals(tr.getType(), "HTTPSamplerProxy")) {
                    MsHTTPSamplerProxy http = (MsHTTPSamplerProxy) tr;
                    String refType = tr.getRefType();
                    if (StringUtils.equals(refType, "CASE")) {
                        http.setUrl(null);
                    } else {
                        ApiDefinition apiDefinition = apiDefinitionService.get(tr.getId());
                        if (apiDefinition != null) {
                            http.setUrl(apiDefinition.getPath());
                        }
                    }
                    if (http.isEnable()) {
                        if (StringUtils.isBlank(http.getUrl()) || !tr.isURL(http.getUrl())) {
                            env.getProjectIds().add(http.getProjectId());
                            env.setFullUrl(false);
                        }
                    }
                } else if (StringUtils.equals(tr.getType(), "JDBCSampler") || StringUtils.equals(tr.getType(), "TCPSampler")) {
                    if (StringUtils.equals(tr.getRefType(), "CASE")) {
                        ApiTestCaseWithBLOBs apiTestCaseWithBLOBs = apiTestCaseService.get(tr.getId());
                        if (apiTestCaseWithBLOBs != null) {
                            env.getProjectIds().add(apiTestCaseWithBLOBs.getProjectId());
                        }
                    } else {
                        ApiDefinition apiDefinition = apiDefinitionService.get(tr.getId());
                        if (apiDefinition != null) {
                            env.getProjectIds().add(apiDefinition.getProjectId());
                        }
                    }
                } else if (StringUtils.equals(tr.getType(), "scenario")) {
                    if (tr.isEnable()) {
                        ApiScenarioWithBLOBs apiScenario = getApiScenario(tr.getId());
                        if (apiScenario != null) {
                            env.getProjectIds().add(apiScenario.getProjectId());
                            String scenarioDefinition = apiScenario.getScenarioDefinition();
                            tr.setHashTree(getScenarioHashTree(scenarioDefinition));
                        }
                    }
                }
            } else {
                if (StringUtils.equals(tr.getType(), "HTTPSamplerProxy")) {
                    // 校验是否是全路径
                    MsHTTPSamplerProxy httpSamplerProxy = (MsHTTPSamplerProxy) tr;
                    if (httpSamplerProxy.isEnable()) {
                        if (StringUtils.isBlank(httpSamplerProxy.getUrl()) || !tr.isURL(httpSamplerProxy.getUrl())) {
                            env.getProjectIds().add(httpSamplerProxy.getProjectId());
                            env.setFullUrl(false);
                        }
                    }
                } else if (StringUtils.equals(tr.getType(), "JDBCSampler") || StringUtils.equals(tr.getType(), "TCPSampler")) {
                    env.getProjectIds().add(tr.getProjectId());
                }
            }
            if (!tr.isEnable()) {
                continue;
            }
            if (StringUtils.equals(tr.getType(), "scenario")) {
                env.getProjectIds().add(tr.getProjectId());
            }
            if (CollectionUtils.isNotEmpty(tr.getHashTree())) {
                getHashTree(tr.getHashTree(), env);
            }
        }
        return env;
    }

    private void getHashTree(List<MsTestElement> tree, ScenarioEnv env) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (int i = 0; i < tree.size(); i++) {
                MsTestElement tr = tree.get(i);
                String referenced = tr.getReferenced();
                if (StringUtils.equals(MsTestElementConstants.REF.name(), referenced)) {
                    if (StringUtils.equals(tr.getType(), "HTTPSamplerProxy")) {
                        MsHTTPSamplerProxy http = (MsHTTPSamplerProxy) tr;
                        String refType = tr.getRefType();
                        if (StringUtils.equals(refType, "CASE")) {
                            http.setUrl(null);
                        } else {
                            ApiDefinition apiDefinition = apiDefinitionService.get(tr.getId());
                            http.setUrl(apiDefinition.getPath());
                        }
                        if (http.isEnable()) {
                            if (StringUtils.isBlank(http.getUrl()) || !tr.isURL(http.getUrl())) {
                                env.setFullUrl(false);
                                env.getProjectIds().add(http.getProjectId());
                            }
                        }
                    } else if (StringUtils.equals(tr.getType(), "JDBCSampler") || StringUtils.equals(tr.getType(), "TCPSampler")) {
                        if (StringUtils.equals(tr.getRefType(), "CASE")) {
                            ApiTestCaseWithBLOBs apiTestCaseWithBLOBs = apiTestCaseService.get(tr.getId());
                            env.getProjectIds().add(apiTestCaseWithBLOBs.getProjectId());
                        } else {
                            ApiDefinition apiDefinition = apiDefinitionService.get(tr.getId());
                            env.getProjectIds().add(apiDefinition.getProjectId());
                        }
                    } else if (StringUtils.equals(tr.getType(), "scenario")) {
                        if (tr.isEnable()) {
                            ApiScenarioWithBLOBs apiScenario = getApiScenario(tr.getId());
                            if (apiScenario != null) {
                                env.getProjectIds().add(apiScenario.getProjectId());
                                String scenarioDefinition = apiScenario.getScenarioDefinition();
                                JSONObject element1 = JSON.parseObject(scenarioDefinition);
                                LinkedList<MsTestElement> hashTree1 = mapper.readValue(element1.getString("hashTree"), new TypeReference<LinkedList<MsTestElement>>() {
                                });
                                tr.setHashTree(hashTree1);
                            }
                        }
                    }
                } else {
                    if (StringUtils.equals(tr.getType(), "HTTPSamplerProxy")) {
                        // 校验是否是全路径
                        MsHTTPSamplerProxy httpSamplerProxy = (MsHTTPSamplerProxy) tr;
                        if (httpSamplerProxy.isEnable()) {
                            if (StringUtils.isBlank(httpSamplerProxy.getUrl()) || !tr.isURL(httpSamplerProxy.getUrl())) {
                                env.setFullUrl(false);
                                env.getProjectIds().add(httpSamplerProxy.getProjectId());
                            }
                        }
                    } else if (StringUtils.equals(tr.getType(), "JDBCSampler") || StringUtils.equals(tr.getType(), "TCPSampler")) {
                        env.getProjectIds().add(tr.getProjectId());
                    }
                }
                if (!tr.isEnable()) {
                    continue;
                }
                if (StringUtils.equals(tr.getType(), "scenario")) {
                    env.getProjectIds().add(tr.getProjectId());
                }
                if (CollectionUtils.isNotEmpty(tr.getHashTree())) {
                    getHashTree(tr.getHashTree(), env);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public List<ApiScenarioWithBLOBs> getApiScenarios(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return extApiScenarioMapper.selectIds(ids);
        }
        return new ArrayList<>();
    }

    public byte[] loadFileAsBytes(FileOperationRequest fileOperationRequest) {
        File file = new File(FileUtils.BODY_FILE_DIR + "/" + fileOperationRequest.getId() + "_" + fileOperationRequest.getName());
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);) {
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            return bos.toByteArray();
        } catch (Exception ex) {
            LogUtil.error(ex.getMessage());
        }
        return null;
    }

    public APIScenarioReportResult createScenarioReport(String id, String scenarioId, String scenarioName, String triggerMode, String execType, String projectId, String userID, RunModeConfig config) {
        APIScenarioReportResult report = new APIScenarioReportResult();
        if (triggerMode.equals(ApiRunMode.SCENARIO.name()) || triggerMode.equals(ApiRunMode.DEFINITION.name())) {
            triggerMode = ReportTriggerMode.MANUAL.name();
        }
        report.setId(id);
        report.setTestId(id);
        if (StringUtils.isNotEmpty(scenarioName)) {
            report.setName(scenarioName);
        } else {
            report.setName("场景调试");
        }
        report.setUpdateTime(System.currentTimeMillis());
        report.setCreateTime(System.currentTimeMillis());
        if (config != null && config.getMode().equals(RunModeConstants.SERIAL.toString())) {
            report.setCreateTime(System.currentTimeMillis() + 2000);
            report.setUpdateTime(System.currentTimeMillis() + 2000);
        }
        report.setStatus(APITestStatus.Running.name());
        if (StringUtils.isNotEmpty(userID)) {
            report.setUserId(userID);
            report.setCreateUser(userID);
        } else {
            report.setUserId(SessionUtils.getUserId());
            report.setCreateUser(SessionUtils.getUserId());
        }
        report.setTriggerMode(triggerMode);
        report.setExecuteType(execType);
        report.setProjectId(projectId);
        report.setScenarioName(scenarioName);
        report.setScenarioId(scenarioId);
        return report;
    }


    private void parse(String scenarioDefinition, MsScenario scenario) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JSONObject element = JSON.parseObject(scenarioDefinition);
            // 多态JSON普通转换会丢失内容，需要通过 ObjectMapper 获取
            if (element != null && StringUtils.isNotEmpty(element.getString("hashTree"))) {
                LinkedList<MsTestElement> elements = mapper.readValue(element.getString("hashTree"),
                        new TypeReference<LinkedList<MsTestElement>>() {
                        });
                scenario.setHashTree(elements);
            }
            if (element != null && StringUtils.isNotEmpty(element.getString("variables"))) {
                LinkedList<ScenarioVariable> variables = mapper.readValue(element.getString("variables"),
                        new TypeReference<LinkedList<ScenarioVariable>>() {
                        });
                scenario.setVariables(variables);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        }
    }

    public HashTree generateHashTree(ApiScenarioWithBLOBs item, String reportId, Map<String, String> planEnvMap) {
        HashTree jmeterHashTree = new HashTree();
        MsTestPlan testPlan = new MsTestPlan();
        testPlan.setHashTree(new LinkedList<>());
        try {
            MsThreadGroup group = new MsThreadGroup();
            group.setLabel(item.getName());
            group.setName(reportId);

            MsScenario scenario = JSONObject.parseObject(item.getScenarioDefinition(), MsScenario.class);
            if (planEnvMap.size() > 0) {
                scenario.setEnvironmentMap(planEnvMap);
            }
            parse(item.getScenarioDefinition(), scenario);

            group.setEnableCookieShare(scenario.isEnableCookieShare());
            LinkedList<MsTestElement> scenarios = new LinkedList<>();
            scenarios.add(scenario);

            group.setHashTree(scenarios);
            testPlan.getHashTree().add(group);
        } catch (Exception ex) {
            MSException.throwException(ex.getMessage());
        }

        testPlan.toHashTree(jmeterHashTree, testPlan.getHashTree(), new ParameterConfig());
        return jmeterHashTree;
    }

    private String generateJmx(ApiScenarioWithBLOBs apiScenario) {
        HashTree jmeterHashTree = new ListedHashTree();
        MsTestPlan testPlan = new MsTestPlan();
        testPlan.setName(apiScenario.getName());
        testPlan.setHashTree(new LinkedList<>());
        ParameterConfig config = new ParameterConfig();
        config.setOperating(true);
        try {

            MsScenario scenario = JSONObject.parseObject(apiScenario.getScenarioDefinition(), MsScenario.class);
            if (scenario == null) {
                return null;
            }
            parse(apiScenario.getScenarioDefinition(), scenario);
            // 针对导入的jmx 处理
            if (CollectionUtils.isNotEmpty(scenario.getHashTree()) && (scenario.getHashTree().get(0) instanceof MsJmeterElement)) {
                scenario.toHashTree(jmeterHashTree, scenario.getHashTree(), config);
                return scenario.getJmx(jmeterHashTree);
            } else {
                MsThreadGroup group = new MsThreadGroup();
                group.setLabel(apiScenario.getName());
                group.setName(apiScenario.getName());
                group.setEnableCookieShare(scenario.isEnableCookieShare());
                group.setHashTree(new LinkedList<MsTestElement>() {{
                    this.add(scenario);
                }});
                testPlan.getHashTree().add(group);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            MSException.throwException(ex.getMessage());
        }
        testPlan.toHashTree(jmeterHashTree, testPlan.getHashTree(), config);
        return testPlan.getJmx(jmeterHashTree);
    }

    private void checkEnv(RunScenarioRequest request, List<ApiScenarioWithBLOBs> apiScenarios) {
        if (StringUtils.isNotBlank(request.getRunMode()) && StringUtils.equalsAny(request.getRunMode(), ApiRunMode.SCENARIO.name(), ApiRunMode.SCENARIO_PLAN.name())) {
            StringBuilder builder = new StringBuilder();
            for (ApiScenarioWithBLOBs apiScenarioWithBLOBs : apiScenarios) {
                try {
                    boolean haveEnv = checkScenarioEnv(apiScenarioWithBLOBs);  //检查场景环境
                    if (!haveEnv) {
                        builder.append(apiScenarioWithBLOBs.getName()).append("; ");
                    }
                } catch (Exception e) {
                    MSException.throwException("场景：" + builder.toString() + "运行环境未配置，请检查!");
                }
            }
            if (builder.length() > 0) {
                MSException.throwException("场景：" + builder.toString() + "运行环境未配置，请检查!");
            }
        }
    }

    /**
     * 场景测试执行
     *
     * @param request
     * @return
     */
    public String modeRun(RunScenarioRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));

        List<String> ids = request.getIds();
        //检查是否有正在执行中的情景
//        this.checkScenarioIsRunning(ids);

        StringBuilder idStr = new StringBuilder();
        ids.forEach(item -> {
            idStr.append("\"").append(item).append("\"").append(",");
        });
        List<ApiScenarioWithBLOBs> apiScenarios = extApiScenarioMapper.selectByIds(idStr.toString().substring(0, idStr.toString().length() - 1), "\"" + StringUtils.join(ids, ",") + "\"");
        // 只有一个场景且没有测试步骤，则提示
        if (apiScenarios != null && apiScenarios.size() == 1 && (apiScenarios.get(0).getStepTotal() == null || apiScenarios.get(0).getStepTotal() == 0)) {
            MSException.throwException((apiScenarios.get(0).getName() + "，" + Translator.get("automation_exec_info")));
        }
        // 环境检查
        this.checkEnv(request, apiScenarios);
        if (request.getConfig() != null && request.getConfig().getMode().equals(RunModeConstants.SERIAL.toString())) {
            if (StringUtils.equals(request.getConfig().getReportType(), RunModeConstants.SET_REPORT.toString()) && StringUtils.isNotEmpty(request.getConfig().getReportName())) {
                request.setExecuteType(ExecuteType.Completed.name());
            }
        }
        if (StringUtils.isEmpty(request.getTriggerMode())) {
            request.setTriggerMode(ReportTriggerMode.MANUAL.name());
        }
        String reportId = request.getId();
        Map<APIScenarioReportResult, HashTree> map = new LinkedHashMap<>();
        List<String> scenarioIds = new ArrayList<>();
        StringBuilder scenarioNames = new StringBuilder();
        // 按照场景执行
        for (ApiScenarioWithBLOBs item : apiScenarios) {
            if (item.getStepTotal() == null || item.getStepTotal() == 0) {
                continue;
            }
            APIScenarioReportResult report;
            Map<String, String> planEnvMap = new HashMap<>();
            //如果是测试计划页面触发的执行方式，生成报告时createScenarioReport第二个参数需要特殊处理
            if (StringUtils.equalsAny(request.getRunMode(), ApiRunMode.SCENARIO_PLAN.name(), ApiRunMode.SCHEDULE_SCENARIO_PLAN.name())) {
                String testPlanScenarioId = item.getId();
                if (request.getScenarioTestPlanIdMap() != null && request.getScenarioTestPlanIdMap().containsKey(item.getId())) {
                    testPlanScenarioId = request.getScenarioTestPlanIdMap().get(item.getId());
                    // 获取场景用例单独的执行环境
                    TestPlanApiScenario planApiScenario = testPlanApiScenarioMapper.selectByPrimaryKey(testPlanScenarioId);
                    String environment = planApiScenario.getEnvironment();
                    if (StringUtils.isNotBlank(environment)) {
                        planEnvMap = JSON.parseObject(environment, Map.class);
                    }
                }
                if (request.isTestPlanScheduleJob()) {
                    String savedScenarioId = testPlanScenarioId + ":" + request.getTestPlanReportId();
                    report = createScenarioReport(reportId, savedScenarioId, item.getName(), request.getTriggerMode(),
                            request.getExecuteType(), item.getProjectId(), request.getReportUserID(), null);
                } else {
                    report = createScenarioReport(reportId, testPlanScenarioId, item.getName(), request.getTriggerMode(),
                            request.getExecuteType(), item.getProjectId(), request.getReportUserID(), null);
                }
            } else {
                report = createScenarioReport(reportId, item.getId(), item.getName(), request.getTriggerMode(),
                        request.getExecuteType(), item.getProjectId(), request.getReportUserID(), null);
            }

            // 生成报告和HashTree
            HashTree hashTree = null;
            try {
                hashTree = generateHashTree(item, reportId, planEnvMap);
            } catch (Exception ex) {
                MSException.throwException("解析运行步骤失败！场景名称：" + item.getName());
            }
            map.put(report, hashTree);
            scenarioIds.add(item.getId());
            scenarioNames.append(item.getName()).append(",");
            // 重置报告ID
            reportId = UUID.randomUUID().toString();
        }
        // 生成集成报告
        String serialReportId = null;
        if (request.getConfig() != null && request.getConfig().getMode().equals(RunModeConstants.SERIAL.toString()) && StringUtils.equals(request.getConfig().getReportType(), RunModeConstants.SET_REPORT.toString()) && StringUtils.isNotEmpty(request.getConfig().getReportName())) {
            request.getConfig().setReportId(UUID.randomUUID().toString());
            APIScenarioReportResult report = createScenarioReport(request.getConfig().getReportId(), JSON.toJSONString(scenarioIds), scenarioNames.deleteCharAt(scenarioNames.toString().length() - 1).toString(), ReportTriggerMode.MANUAL.name(),
                    ExecuteType.Saved.name(), request.getProjectId(), request.getReportUserID(), request.getConfig());
            report.setName(request.getConfig().getReportName());
            apiScenarioReportMapper.insert(report);  //保存执行的报告
            serialReportId = report.getId();   //报告id
        }
        // 开始执行
        if (!map.isEmpty()) {
            try {
                this.run(map, request, serialReportId);
            } catch (Exception ex) {
                LogUtil.error("场景执行异常：" + ex);
                MSException.throwException(ex.getMessage());
            }
        }
        return request.getId();
    }

    public void run(Map<APIScenarioReportResult, HashTree> map, RunScenarioRequest request, String serialReportId) {
        // 开始选择执行模式
        ExecutorService executorService = Executors.newFixedThreadPool(cpuCount/2);
        if (!map.isEmpty() && request.getConfig() != null && request.getConfig().getMode().equals(RunModeConstants.SERIAL.toString())) {
            // 开始串行执行
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> reportIds = new LinkedList<>();
                    for (APIScenarioReportResult key : map.keySet()) {
                        apiScenarioReportMapper.insert(key);
                        reportIds.add(key.getId());
                        try {
                            //提交任务并返回Future对象
                            Future<ApiScenarioReport> future = executorService.submit(new SerialScenarioExecTask(jMeterService, apiScenarioReportMapper, key.getId(), map.get(key), request));
                            ApiScenarioReport report = future.get(); //从Future中获取执行结果--也就是场景执行报告
                            // 如果开启失败结束执行，则判断返回结果状态
                            if (request.getConfig().isOnSampleError()) {
                                if (report == null || !report.getStatus().equals("Success")) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LogUtil.error("执行终止：" + e.getMessage());
                            break;
                        }
                    }
                    // 更新集成报告
                    if (StringUtils.isNotEmpty(serialReportId)) {
                        apiScenarioReportService.margeReport(serialReportId, reportIds);// 合并报告
                        map.clear();
                    }
                }
            });
            thread.setName("Scenario Serial thread");
            thread.start();
        } else {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            ApiScenarioReportMapper batchMapper = sqlSession.getMapper(ApiScenarioReportMapper.class);
            // 开始并发执行
            for (APIScenarioReportResult report : map.keySet()) {
                //存储报告
                batchMapper.insert(report);
                executorService.submit(new ParallelScenarioExecTask(jMeterService, report.getId(), map.get(report), request));
            }
            sqlSession.flushStatements();
        }
        executorService.shutdown();
        while (true){
            if(executorService.isTerminated()){
                break;
            }
        }
    }

    /**
     * 生成HashTree
     *
     * @param apiScenarios 场景
     * @param request      请求参数
     * @param reportIds    报告ID
     * @return hashTree
     */
    private HashTree generateHashTree(List<ApiScenarioWithBLOBs> apiScenarios, RunScenarioRequest request, List<String> reportIds) {
        HashTree jmeterHashTree = new ListedHashTree();
        MsTestPlan testPlan = new MsTestPlan();
        testPlan.setHashTree(new LinkedList<>());
        try {
            boolean isFirst = true;
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            ApiScenarioReportMapper batchMapper = sqlSession.getMapper(ApiScenarioReportMapper.class);
            for (ApiScenarioWithBLOBs item : apiScenarios) {
                if (item.getStepTotal() == null || item.getStepTotal() == 0) {
                    // 只有一个场景且没有测试步骤，则提示
                    if (apiScenarios.size() == 1) {
                        MSException.throwException((item.getName() + "，" + Translator.get("automation_exec_info")));
                    }
                    LogUtil.warn(item.getName() + "，" + Translator.get("automation_exec_info"));
                    continue;
                }
                MsThreadGroup group = new MsThreadGroup();
                group.setLabel(item.getName());
                group.setName(UUID.randomUUID().toString());
                if (request.getConfig() != null) {
                    group.setOnSampleError(request.getConfig().isOnSampleError());
                }
                // 批量执行的结果直接存储为报告
                if (isFirst && StringUtils.isNotEmpty(request.getId())) {
                    group.setName(request.getId());
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONObject element = JSON.parseObject(item.getScenarioDefinition());
                MsScenario scenario = JSONObject.parseObject(item.getScenarioDefinition(), MsScenario.class);

                // 多态JSON普通转换会丢失内容，需要通过 ObjectMapper 获取
                if (element != null && StringUtils.isNotEmpty(element.getString("hashTree"))) {
                    LinkedList<MsTestElement> elements = mapper.readValue(element.getString("hashTree"),
                            new TypeReference<LinkedList<MsTestElement>>() {
                            });
                    scenario.setHashTree(elements);
                }
                if (StringUtils.isNotEmpty(element.getString("variables"))) {
                    LinkedList<ScenarioVariable> variables = mapper.readValue(element.getString("variables"),
                            new TypeReference<LinkedList<ScenarioVariable>>() {
                            });
                    scenario.setVariables(variables);
                }
                group.setEnableCookieShare(scenario.isEnableCookieShare());
                LinkedList<MsTestElement> scenarios = new LinkedList<>();
                scenarios.add(scenario);
                // 创建场景报告
                if (reportIds != null) {
                    //如果是测试计划页面触发的执行方式，生成报告时createScenarioReport第二个参数需要特殊处理
                    APIScenarioReportResult report = null;
//                  if (StringUtils.equals(request.getRunMode(), ApiRunMode.SCENARIO_PLAN.name())) {
                    if (StringUtils.equalsAny(request.getRunMode(), ApiRunMode.SCENARIO_PLAN.name(), ApiRunMode.SCHEDULE_SCENARIO_PLAN.name())) {
                        String testPlanScenarioId = item.getId();
                        if (request.getScenarioTestPlanIdMap() != null && request.getScenarioTestPlanIdMap().containsKey(item.getId())) {
                            testPlanScenarioId = request.getScenarioTestPlanIdMap().get(item.getId());
                            // 获取场景用例单独的执行环境
                            TestPlanApiScenario planApiScenario = testPlanApiScenarioMapper.selectByPrimaryKey(testPlanScenarioId);
                            String environment = planApiScenario.getEnvironment();
                            if (StringUtils.isNotBlank(environment)) {
                                scenario.setEnvironmentMap(JSON.parseObject(environment, Map.class));
                            }
                        }
                        if (request.isTestPlanScheduleJob()) {
                            String savedScenarioId = testPlanScenarioId + ":" + request.getTestPlanReportId();
                            report = createScenarioReport(group.getName(), savedScenarioId, item.getName(), request.getTriggerMode(),
                                    request.getExecuteType(), item.getProjectId(), request.getReportUserID(), null);
                        } else {
                            report = createScenarioReport(group.getName(), testPlanScenarioId, item.getName(), request.getTriggerMode() == null ? ReportTriggerMode.MANUAL.name() : request.getTriggerMode(),
                                    request.getExecuteType(), item.getProjectId(), request.getReportUserID(), request.getConfig());
                        }
                    } else {
                        report = createScenarioReport(group.getName(), item.getId(), item.getName(), request.getTriggerMode() == null ? ReportTriggerMode.MANUAL.name() : request.getTriggerMode(),
                                request.getExecuteType(), item.getProjectId(), request.getReportUserID(), request.getConfig());
                    }
                    batchMapper.insert(report);
                    reportIds.add(group.getName());
                }
                group.setHashTree(scenarios);
                testPlan.getHashTree().add(group);
                isFirst = false;
            }
            testPlan.toHashTree(jmeterHashTree, testPlan.getHashTree(), new ParameterConfig());
            sqlSession.flushStatements();
        } catch (Exception ex) {
            MSException.throwException(ex.getMessage());
        }

        return jmeterHashTree;
    }

    private boolean checkScenarioEnv(ApiScenarioWithBLOBs apiScenarioWithBLOBs) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String definition = apiScenarioWithBLOBs.getScenarioDefinition();//取出场景中场景定义的部分
        MsScenario scenario = JSONObject.parseObject(definition, MsScenario.class);  //使用jsonobject处理
        boolean isEnv = true;
        Map<String, String> envMap = scenario.getEnvironmentMap();  //拿到场景定义里面的环境map
        ScenarioEnv apiScenarioEnv = getApiScenarioEnv(definition);
        // 所有请求非全路径检查环境
        if (!apiScenarioEnv.getFullUrl()) {
            try {
                if (envMap == null || envMap.isEmpty()) {
                    isEnv = false;
                } else {
                    Set<String> projectIds = apiScenarioEnv.getProjectIds();
                    projectIds.remove(null);
                    if (CollectionUtils.isNotEmpty(envMap.keySet())) {
                        for (String id : projectIds) {
                            Project project = projectMapper.selectByPrimaryKey(id);
                            if (project == null) {
                                id = apiScenarioWithBLOBs.getProjectId();
                            }
                            String s = envMap.get(id);
                            if (StringUtils.isBlank(s)) {
                                isEnv = false;
                                break;
                            } else {
                                ApiTestEnvironmentWithBLOBs env = apiTestEnvironmentMapper.selectByPrimaryKey(s);
                                if (env == null) {
                                    isEnv = false;
                                    break;
                                }
                            }
                        }
                    } else {
                        isEnv = false;
                    }
                }
            } catch (Exception e) {
                isEnv = false;
                LogUtil.error(e.getMessage(), e);
            }
        }

        // 1.8 之前环境是 environmentId
        if (!isEnv) {
            String envId = scenario.getEnvironmentId();
            if (StringUtils.isNotBlank(envId)) {
                ApiTestEnvironmentWithBLOBs env = apiTestEnvironmentMapper.selectByPrimaryKey(envId);
                if (env != null) {
                    isEnv = true;
                }
            }
        }
        return isEnv;
    }

    /**
     * 串行，利用JMETER自身串行机制执行
     *
     * @param request
     * @return
     */
    public String excute(RunScenarioRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));
        List<String> ids = request.getIds();  //场景id
        //检查是否有正在执行中的情景
//        this.checkScenarioIsRunning(ids);
        StringBuilder idStr = new StringBuilder();
        ids.forEach(item -> {
            idStr.append("\"").append(item).append("\"").append(",");
        });
        //获取场景信息
        List<ApiScenarioWithBLOBs> apiScenarios = extApiScenarioMapper.selectByIds(idStr.toString().substring(0, idStr.toString().length() - 1), "\"" + StringUtils.join(ids, ",") + "\"");
        String runMode = ApiRunMode.SCENARIO.name();
        if (StringUtils.isNotBlank(request.getRunMode()) && StringUtils.equals(request.getRunMode(), ApiRunMode.SCENARIO_PLAN.name())) {
            runMode = ApiRunMode.SCENARIO_PLAN.name();
        }
        if (StringUtils.isNotBlank(request.getRunMode()) && StringUtils.equals(request.getRunMode(), ApiRunMode.DEFINITION.name())) {
            runMode = ApiRunMode.DEFINITION.name();
        }

        // 环境检查
        this.checkEnv(request, apiScenarios);

        // 调用执行方法
        List<String> reportIds = new LinkedList<>();
        try {
            HashTree hashTree = generateHashTree(apiScenarios, request, reportIds);
            if (request.getConfig() != null && StringUtils.isNotBlank(request.getConfig().getResourcePoolId())) {
                jMeterService.runTest(JSON.toJSONString(reportIds), hashTree, runMode, false, request.getConfig());
            } else {
                //串行运行
                jMeterService.runSerial(JSON.toJSONString(reportIds), hashTree, request.getReportId(), runMode, request.getConfig());
            }

        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            MSException.throwException(e.getMessage());
        }
        return request.getId();
    }

    public String run(RunScenarioRequest request) {
        if (request.getConfig() != null) {
            if (request.getConfig().getMode().equals(RunModeConstants.PARALLEL.toString())) {
                // 校验并发数量
                int count = 50;
                BaseSystemConfigDTO dto = systemParameterService.getBaseInfo();
                if (StringUtils.isNotEmpty(dto.getConcurrency())) {
                    count = Integer.parseInt(dto.getConcurrency());
                }
                if (request.getIds().size() > count) {
                    MSException.throwException("并发数量过大，请重新选择！");
                }
                return this.modeRun(request);
            } else {
                return this.modeRun(request);
            }
        } else {
            return this.excute(request);
        }
    }

//    public void checkScenarioIsRunning(List<String> ids) {
//        List<ApiScenarioReport> lastReportStatusByIds = apiReportService.selectLastReportByIds(ids);
//        for (ApiScenarioReport report : lastReportStatusByIds) {
//            if (StringUtils.equals(report.getStatus(), APITestStatus.Running.name())) {
//                MSException.throwException(report.getName() + " Is Running!");
//            }
//        }
//    }

    /**
     * 获取前台查询条件查询的所有(未经分页筛选)数据ID
     *
     * @param moduleIds   模块ID_前台查询时所选择的
     * @param name        搜索条件_名称_前台查询时所输入的
     * @param projectId   所属项目_前台查询时所在项目
     * @param filters     过滤集合__前台查询时的过滤条件
     * @param unSelectIds 未勾选ID_前台没有勾选的ID
     * @return
     */
    private List<String> getAllScenarioIdsByFontedSelect(List<String> moduleIds, String name, String projectId, Map<String, List<String>> filters, List<String> unSelectIds) {
        ApiScenarioRequest selectRequest = new ApiScenarioRequest();
        selectRequest.setModuleIds(moduleIds);
        selectRequest.setName(name);
        selectRequest.setProjectId(projectId);
        selectRequest.setFilters(filters);
        selectRequest.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        List<ApiScenarioDTO> list = extApiScenarioMapper.list(selectRequest);
        List<String> allIds = list.stream().map(ApiScenarioDTO::getId).collect(Collectors.toList());
        List<String> ids = allIds.stream().filter(id -> !unSelectIds.contains(id)).collect(Collectors.toList());
        return ids;
    }

    /**
     * 场景测试执行
     *
     * @param request
     * @param bodyFiles
     * @return
     */
    public String debugRun(RunDefinitionRequest request, List<MultipartFile> bodyFiles, List<MultipartFile> scenarioFiles) {
        Map<String, EnvironmentConfig> envConfig = new HashMap<>();
        Map<String, String> map = request.getEnvironmentMap();
        if (map != null) {
            map.keySet().forEach(id -> {
                ApiTestEnvironmentWithBLOBs environment = environmentService.get(map.get(id));
                EnvironmentConfig env = JSONObject.parseObject(environment.getConfig(), EnvironmentConfig.class);
                env.setApiEnvironmentid(environment.getId());
                envConfig.put(id, env);
            });
        }
        ParameterConfig config = new ParameterConfig();
        config.setConfig(envConfig);
        HashTree hashTree = null;
        try {
            hashTree = request.getTestElement().generateHashTree(config);
            LogUtil.info(request.getTestElement().getJmx(hashTree));
        } catch (Exception e) {
            MSException.throwException(e.getMessage());
        }

        APIScenarioReportResult report = createScenarioReport(request.getId(), request.getScenarioId(), request.getScenarioName(), ReportTriggerMode.MANUAL.name(), request.getExecuteType(), request.getProjectId(),
                SessionUtils.getUserId(), null);
        apiScenarioReportMapper.insert(report);

        uploadBodyFiles(request.getBodyFileRequestIds(), bodyFiles);
        FileUtils.createBodyFiles(request.getScenarioFileIds(), scenarioFiles);

        // 调用执行方法
        jMeterService.runDefinition(request.getId(), hashTree, request.getReportId(), ApiRunMode.SCENARIO.name());
        return request.getId();
    }

    public ReferenceDTO getReference(ApiScenarioRequest request) {
        ReferenceDTO dto = new ReferenceDTO();
        dto.setScenarioList(extApiScenarioMapper.selectReference(request));
        QueryTestPlanRequest planRequest = new QueryTestPlanRequest();
        planRequest.setScenarioId(request.getId());
        planRequest.setProjectId(request.getProjectId());
        dto.setTestPlanList(extTestPlanMapper.selectTestPlanByRelevancy(planRequest));
        return dto;
    }


    public String addScenarioToPlan(SaveApiPlanRequest request) {
        if (CollectionUtils.isEmpty(request.getPlanIds())) {
            MSException.throwException(Translator.get("plan id is null "));
        }
        Map<String, List<String>> mapping = request.getMapping();
        Map<String, String> envMap = request.getEnvMap();
        Set<String> set = mapping.keySet();
        List<TestPlanDTO> list = extTestPlanMapper.selectByIds(request.getPlanIds());
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        ExtTestPlanScenarioCaseMapper scenarioBatchMapper = sqlSession.getMapper(ExtTestPlanScenarioCaseMapper.class);
        ExtTestPlanApiCaseMapper apiCaseBatchMapper = sqlSession.getMapper(ExtTestPlanApiCaseMapper.class);

        for (TestPlanDTO testPlan : list) {
            if (!set.isEmpty()) {
                set.forEach(id -> {
                    Map<String, String> newEnvMap = new HashMap<>(16);
                    if (envMap != null && !envMap.isEmpty()) {
                        List<String> lt = mapping.get(id);
                        lt.forEach(l -> {
                            newEnvMap.put(l, envMap.get(l));
                        });
                    }
                    TestPlanApiScenario testPlanApiScenario = new TestPlanApiScenario();
                    testPlanApiScenario.setId(UUID.randomUUID().toString());
                    testPlanApiScenario.setApiScenarioId(id);
                    testPlanApiScenario.setTestPlanId(testPlan.getId());
                    testPlanApiScenario.setCreateTime(System.currentTimeMillis());
                    testPlanApiScenario.setUpdateTime(System.currentTimeMillis());
                    testPlanApiScenario.setEnvironment(JSON.toJSONString(newEnvMap));
                    scenarioBatchMapper.insertIfNotExists(testPlanApiScenario);
                });
            }
            if (request.getApiIds() != null) {
                for (String caseId : request.getApiIds()) {
                    TestPlanApiCase testPlanApiCase = new TestPlanApiCase();
                    testPlanApiCase.setId(UUID.randomUUID().toString());
                    testPlanApiCase.setApiCaseId(caseId);
                    testPlanApiCase.setTestPlanId(testPlan.getId());
                    testPlanApiCase.setCreateTime(System.currentTimeMillis());
                    testPlanApiCase.setUpdateTime(System.currentTimeMillis());
                    apiCaseBatchMapper.insertIfNotExists(testPlanApiCase);
                }
            }
        }
        sqlSession.flushStatements();
        return "success";
    }

    public long countScenarioByProjectID(String projectId) {
        return extApiScenarioMapper.countByProjectID(projectId);
    }

    public List<ApiScenarioWithBLOBs> selectIdAndScenarioByProjectId(String projectId) {
        return extApiScenarioMapper.selectIdAndScenarioByProjectId(projectId);
    }

    public long countScenarioByProjectIDAndCreatInThisWeek(String projectId) {
        Map<String, Date> startAndEndDateInWeek = DateUtils.getWeedFirstTimeAndLastTime(new Date());
        Date firstTime = startAndEndDateInWeek.get("firstTime");
        Date lastTime = startAndEndDateInWeek.get("lastTime");
        if (firstTime == null || lastTime == null) {
            return 0;
        } else {
            return extApiScenarioMapper.countByProjectIDAndCreatInThisWeek(projectId, firstTime.getTime(), lastTime.getTime());
        }
    }

    public List<ApiDataCountResult> countRunResultByProjectID(String projectId) {
        return extApiScenarioMapper.countRunResultByProjectID(projectId);
    }

    public void relevance(ApiCaseRelevanceRequest request) {
        Map<String, List<String>> mapping = request.getMapping();
        Map<String, String> envMap = request.getEnvMap();
        Set<String> set = mapping.keySet();
        if (set.isEmpty()) {
            return;
        }
        set.forEach(id -> {
            Map<String, String> newEnvMap = new HashMap<>(16);
            if (envMap != null && !envMap.isEmpty()) {
                List<String> list = mapping.get(id);
                list.forEach(l -> {
                    newEnvMap.put(l, envMap.get(l));
                });
            }
            TestPlanApiScenario testPlanApiScenario = new TestPlanApiScenario();
            testPlanApiScenario.setId(UUID.randomUUID().toString());
            testPlanApiScenario.setCreateUser(SessionUtils.getUserId());
            testPlanApiScenario.setApiScenarioId(id);
            testPlanApiScenario.setTestPlanId(request.getPlanId());
            testPlanApiScenario.setCreateTime(System.currentTimeMillis());
            testPlanApiScenario.setUpdateTime(System.currentTimeMillis());
            testPlanApiScenario.setEnvironment(JSON.toJSONString(newEnvMap));
            testPlanApiScenarioMapper.insert(testPlanApiScenario);
        });
    }

    public void relevanceReview(ApiCaseRelevanceRequest request) {
        Map<String, List<String>> mapping = request.getMapping();
        Map<String, String> envMap = request.getEnvMap();
        Set<String> set = mapping.keySet();
        if (set.isEmpty()) {
            return;
        }
        set.forEach(id -> {
            Map<String, String> newEnvMap = new HashMap<>(16);
            if (envMap != null && !envMap.isEmpty()) {
                List<String> list = mapping.get(id);
                list.forEach(l -> {
                    newEnvMap.put(l, envMap.get(l));
                });
            }
            TestCaseReviewScenario testCaseReviewScenario = new TestCaseReviewScenario();
            testCaseReviewScenario.setId(UUID.randomUUID().toString());
            testCaseReviewScenario.setApiScenarioId(id);
            testCaseReviewScenario.setTestCaseReviewId(request.getReviewId());
            testCaseReviewScenario.setCreateTime(System.currentTimeMillis());
            testCaseReviewScenario.setUpdateTime(System.currentTimeMillis());
            testCaseReviewScenario.setEnvironment(JSON.toJSONString(newEnvMap));
            testCaseReviewScenarioMapper.insert(testCaseReviewScenario);
        });
    }

    public List<ApiScenario> selectByIds(List<String> ids) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andIdIn(ids);
        return apiScenarioMapper.selectByExample(example);
    }

    public List<ApiScenarioWithBLOBs> selectByIdsWithBLOBs(List<String> ids) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andIdIn(ids);
        return apiScenarioMapper.selectByExampleWithBLOBs(example);
    }

    public void createSchedule(ScheduleRequest request) {
        Schedule schedule = scheduleService.buildApiTestSchedule(request);
        ApiScenarioWithBLOBs apiScene = apiScenarioMapper.selectByPrimaryKey(request.getResourceId());
        schedule.setName(apiScene.getName());   //  add场景定时任务时，设置新增的数据库表字段的值
        schedule.setProjectId(apiScene.getProjectId());
        schedule.setJob(ApiScenarioTestJob.class.getName());
        schedule.setGroup(ScheduleGroup.API_SCENARIO_TEST.name());
        schedule.setType(ScheduleType.CRON.name());
        scheduleService.addSchedule(schedule);
        this.addOrUpdateApiScenarioCronJob(request);
    }

    public void updateSchedule(Schedule request) {
        scheduleService.editSchedule(request);
        this.addOrUpdateApiScenarioCronJob(request);
    }

    private void addOrUpdateApiScenarioCronJob(Schedule request) {
        if (StringUtils.equals(request.getGroup(), ScheduleGroup.TEST_PLAN_TEST.name())) {
            scheduleService.addOrUpdateCronJob(
                    request, TestPlanTestJob.getJobKey(request.getResourceId()), TestPlanTestJob.getTriggerKey(request.getResourceId()), TestPlanTestJob.class);
        } else if (StringUtils.equals(request.getGroup(), ScheduleGroup.SWAGGER_IMPORT.name())) {
            scheduleService.addOrUpdateCronJob(request, SwaggerUrlImportJob.getJobKey(request.getResourceId()), SwaggerUrlImportJob.getTriggerKey(request.getResourceId()), SwaggerUrlImportJob.class);
        } else {
            scheduleService.addOrUpdateCronJob(
                    request, ApiScenarioTestJob.getJobKey(request.getResourceId()), ApiScenarioTestJob.getTriggerKey(request.getResourceId()), ApiScenarioTestJob.class);
        }

    }

    public JmxInfoDTO genPerformanceTestJmx(RunScenarioRequest request) throws Exception {
        List<ApiScenarioWithBLOBs> apiScenarios = null;
        List<String> ids = request.getIds();
        apiScenarios = extApiScenarioMapper.selectIds(ids);
        String testName = "";
        if (!apiScenarios.isEmpty()) {
            testName = apiScenarios.get(0).getName();
        }
        if (CollectionUtils.isEmpty(apiScenarios)) {
            return null;
        }
        MsTestPlan testPlan = new MsTestPlan();
        testPlan.setHashTree(new LinkedList<>());
        JmxInfoDTO dto = apiTestService.updateJmxString(generateJmx(apiScenarios.get(0)), testName, true);

        String name = request.getName() + ".jmx";
        dto.setName(name);
        return dto;
    }

    public void bathEdit(ApiScenarioBatchRequest request) {

        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));

        if (StringUtils.isNotBlank(request.getEnvironmentId())) {
            bathEditEnv(request);
            return;
        }
        ApiScenarioExample apiScenarioExample = new ApiScenarioExample();
        apiScenarioExample.createCriteria().andIdIn(request.getIds());
        ApiScenarioWithBLOBs apiScenarioWithBLOBs = new ApiScenarioWithBLOBs();
        BeanUtils.copyBean(apiScenarioWithBLOBs, request);
        apiScenarioWithBLOBs.setUpdateTime(System.currentTimeMillis());
        apiScenarioMapper.updateByExampleSelective(
                apiScenarioWithBLOBs,
                apiScenarioExample);
    }

    public void bathEditEnv(ApiScenarioBatchRequest request) {
        if (StringUtils.isNotBlank(request.getEnvironmentId())) {
            List<ApiScenarioWithBLOBs> apiScenarios = selectByIdsWithBLOBs(request.getIds());
            apiScenarios.forEach(item -> {
                JSONObject object = JSONObject.parseObject(item.getScenarioDefinition());
                object.put("environmentId", request.getEnvironmentId());
                if (object != null) {
                    item.setScenarioDefinition(JSONObject.toJSONString(object));
                }
                apiScenarioMapper.updateByPrimaryKeySelective(item);
            });
        }
    }

    public List<ApiScenarioWithBLOBs> getWithBLOBs(ApiScenarioWithBLOBs request) {
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andNameEqualTo(request.getName()).andProjectIdEqualTo(request.getProjectId()).andStatusNotEqualTo("Trash").andIdNotEqualTo(request.getId());
        return apiScenarioMapper.selectByExampleWithBLOBs(example);
    }

    private void _importCreate(List<ApiScenarioWithBLOBs> sameRequest, ApiScenarioMapper batchMapper, ApiScenarioWithBLOBs scenarioWithBLOBs, ApiTestImportRequest apiTestImportRequest) {
        if (CollectionUtils.isEmpty(sameRequest)) {
            scenarioWithBLOBs.setId(UUID.randomUUID().toString());
            batchMapper.insert(scenarioWithBLOBs);
        } else {
            //如果存在则修改
            scenarioWithBLOBs.setId(sameRequest.get(0).getId());
            scenarioWithBLOBs.setNum(sameRequest.get(0).getNum());
            batchMapper.updateByPrimaryKeyWithBLOBs(scenarioWithBLOBs);
        }
    }

    private ApiScenarioWithBLOBs importCreate(ApiScenarioWithBLOBs request, ApiScenarioMapper batchMapper, ApiTestImportRequest apiTestImportRequest) {
        final ApiScenarioWithBLOBs scenarioWithBLOBs = new ApiScenarioWithBLOBs();
        BeanUtils.copyBean(scenarioWithBLOBs, request);
        scenarioWithBLOBs.setCreateTime(System.currentTimeMillis());
        scenarioWithBLOBs.setUpdateTime(System.currentTimeMillis());
        if (StringUtils.isEmpty(scenarioWithBLOBs.getStatus())) {
            scenarioWithBLOBs.setStatus(APITestStatus.Underway.name());
        }
        scenarioWithBLOBs.setProjectId(apiTestImportRequest.getProjectId());
        if (StringUtils.isEmpty(request.getPrincipal())) {
            scenarioWithBLOBs.setPrincipal(Objects.requireNonNull(SessionUtils.getUser()).getId());
        }
        if (request.getUserId() == null) {
            scenarioWithBLOBs.setUserId(Objects.requireNonNull(SessionUtils.getUser()).getId());
        } else {
            scenarioWithBLOBs.setUserId(request.getUserId());
        }
        scenarioWithBLOBs.setDescription(request.getDescription());

        List<ApiScenarioWithBLOBs> sameRequest = getWithBLOBs(scenarioWithBLOBs);
        if (StringUtils.equals("fullCoverage", apiTestImportRequest.getModeId())) {
            _importCreate(sameRequest, batchMapper, scenarioWithBLOBs, apiTestImportRequest);
        } else if (StringUtils.equals("incrementalMerge", apiTestImportRequest.getModeId())) {
            if (CollectionUtils.isEmpty(sameRequest)) {
                batchMapper.insert(scenarioWithBLOBs);
            }

        } else {
            _importCreate(sameRequest, batchMapper, scenarioWithBLOBs, apiTestImportRequest);
        }
        return scenarioWithBLOBs;
    }

    private void editScenario(ApiTestImportRequest request, ScenarioImport apiImport) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        ApiScenarioMapper batchMapper = sqlSession.getMapper(ApiScenarioMapper.class);
        List<ApiScenarioWithBLOBs> data = apiImport.getData();
        int num = 0;
        Project project = new Project();
        if (!CollectionUtils.isEmpty(data) && data.get(0) != null && data.get(0).getProjectId() != null) {
            num = getNextNum(data.get(0).getProjectId());
            project = projectMapper.selectByPrimaryKey(data.get(0).getProjectId());
        }
        for (int i = 0; i < data.size(); i++) {
            ApiScenarioWithBLOBs item = data.get(i);
            if (item.getName().length() > 255) {
                item.setName(item.getName().substring(0, 255));
            }
            item.setNum(num++);
            if (BooleanUtils.isTrue(project.getScenarioCustomNum())) {
                item.setCustomNum(String.valueOf(num));
            }
            importCreate(item, batchMapper, request);
            if (i % 300 == 0) {
                sqlSession.flushStatements();
            }
        }
        sqlSession.flushStatements();
    }

    public ScenarioImport scenarioImport(MultipartFile file, ApiTestImportRequest request) {
        ApiImportParser apiImportParser = ScenarioImportParserFactory.getImportParser(request.getPlatform());
        ScenarioImport apiImport = null;
        Optional.ofNullable(file)
                .ifPresent(item -> request.setFileName(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."))));
        try {
            apiImport = (ScenarioImport) Objects.requireNonNull(apiImportParser).parse(file == null ? null : file.getInputStream(), request);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(Translator.get("parse_data_error"));
        }
        if (apiImport != null) {
            editScenario(request, apiImport);
            if (CollectionUtils.isNotEmpty(apiImport.getData())) {
                List<String> names = apiImport.getData().stream().map(ApiScenarioWithBLOBs::getName).collect(Collectors.toList());
                List<String> ids = apiImport.getData().stream().map(ApiScenarioWithBLOBs::getId).collect(Collectors.toList());
                request.setName(String.join(",", names));
                request.setId(JSON.toJSONString(ids));
            }
        }
        return apiImport;
    }

    private List<ApiScenarioWithBLOBs> getExportResult(ApiScenarioBatchRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));
        ApiScenarioExample example = new ApiScenarioExample();
        example.createCriteria().andIdIn(request.getIds());
        List<ApiScenarioWithBLOBs> apiScenarioWithBLOBs = apiScenarioMapper.selectByExampleWithBLOBs(example);
        return apiScenarioWithBLOBs;
    }

    public ApiScenrioExportResult export(ApiScenarioBatchRequest request) {
        ApiScenrioExportResult result = new ApiScenrioExportResult();
        result.setData(getExportResult(request));
        result.setProjectId(request.getProjectId());
        result.setVersion(System.getenv("MS_VERSION"));
        if (CollectionUtils.isNotEmpty(result.getData())) {
            List<String> names = result.getData().stream().map(ApiScenarioWithBLOBs::getName).collect(Collectors.toList());
            request.setName(String.join(",", names));
            List<String> ids = result.getData().stream().map(ApiScenarioWithBLOBs::getId).collect(Collectors.toList());
            request.setId(JSON.toJSONString(ids));
        }
        return result;
    }

    public List<ApiScenrioExportJmx> exportJmx(ApiScenarioBatchRequest request) {
        List<ApiScenarioWithBLOBs> apiScenarioWithBLOBs = getExportResult(request);
        // 生成jmx
        List<ApiScenrioExportJmx> resList = new ArrayList<>();
        apiScenarioWithBLOBs.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getScenarioDefinition())) {
                String jmx = generateJmx(item);
                if (StringUtils.isNotEmpty(jmx)) {
                    ApiScenrioExportJmx scenrioExportJmx = new ApiScenrioExportJmx(item.getName(), apiTestService.updateJmxString(jmx, null, true).getXml());
                    //扫描需要哪些文件
                    JmxInfoDTO dto = apiTestService.updateJmxString(jmx, item.getName(), true);
                    if (MapUtils.isNotEmpty(dto.getAttachFiles())) {
                        List<String> fileList = new ArrayList<>();
                        for (String fileName : dto.getAttachFiles().values()) {
                            if (!fileList.contains(fileName)) {
                                fileList.add(fileName);
                            }
                        }
                        if (!fileList.isEmpty()) {
                            scenrioExportJmx.setFiles(fileList);
                        }
                    }
                    resList.add(scenrioExportJmx);
                }
            }
        });
        if (CollectionUtils.isNotEmpty(apiScenarioWithBLOBs)) {
            List<String> names = apiScenarioWithBLOBs.stream().map(ApiScenarioWithBLOBs::getName).collect(Collectors.toList());
            request.setName(String.join(",", names));
            List<String> ids = apiScenarioWithBLOBs.stream().map(ApiScenarioWithBLOBs::getId).collect(Collectors.toList());
            request.setId(JSON.toJSONString(ids));
        }
        return resList;
    }

    public void batchUpdateEnv(ApiScenarioBatchRequest request) {
        Map<String, String> envMap = request.getEnvMap();
        Map<String, List<String>> mapping = request.getMapping();
        Set<String> set = mapping.keySet();
        if (set.isEmpty()) {
            return;
        }
        set.forEach(id -> {
            Map<String, String> newEnvMap = new HashMap<>(16);
            if (envMap != null && !envMap.isEmpty()) {
                List<String> list = mapping.get(id);
                list.forEach(l -> {
                    newEnvMap.put(l, envMap.get(l));
                });
            }
            if (!newEnvMap.isEmpty()) {
                ApiScenarioWithBLOBs scenario = apiScenarioMapper.selectByPrimaryKey(id);
                String definition = scenario.getScenarioDefinition();
                JSONObject object = JSON.parseObject(definition);
                object.put("environmentMap", newEnvMap);
                String newDefinition = JSON.toJSONString(object);
                scenario.setScenarioDefinition(newDefinition);
                apiScenarioMapper.updateByPrimaryKeySelective(scenario);
            }
        });
    }

    public void removeToGcByBatch(ApiScenarioBatchRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));

        this.removeToGc(request.getIds());
    }

    public void deleteBatchByCondition(ApiScenarioBatchRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));

        this.deleteBatch(request.getIds());
    }

    /**
     * 统计接口覆盖率
     * 1.场景中复制的接口
     * 2.场景中引用/复制的案例
     * 3.场景中的自定义路径与接口定义中的匹配
     *
     * @param allScenarioInfoList     场景集合（id / scenario大字段 必须有数据）
     * @param allEffectiveApiList     接口集合（id / path 必须有数据）
     * @param allEffectiveApiCaseList 案例集合(id /api_definition_id 必须有数据)
     * @return
     */
    public float countInterfaceCoverage(List<ApiScenarioWithBLOBs> allScenarioInfoList, List<ApiDefinition> allEffectiveApiList, List<ApiTestCase> allEffectiveApiCaseList) {
//        float coverageRageNumber = (float) apiCountResult.getExecutionPassCount() * 100 / allCount;
        float intetfaceCoverage = 0;
        if (allEffectiveApiList == null || allEffectiveApiList.isEmpty()) {
            return 100;
        }

        /**
         * 前置工作：
         *  1。将接口集合转化数据结构: map<url,List<id>> urlMap 用来做3的筛选
         *  2。将案例集合转化数据结构：map<testCase.id,List<testCase.apiId>> caseIdMap 用来做2的筛选
         *  3。将接口集合转化数据结构: List<id> allApiIdList 用来做1的筛选
         *  4。自定义List<api.id> coveragedIdList 已覆盖的id集合。 最终计算公式是 coveragedIdList/allApiIdList在
         *
         * 解析allScenarioList的scenarioDefinition字段。
         * 1。提取每个步骤的url。 在 urlMap筛选
         * 2。提取每个步骤的id.   在caseIdMap 和 allApiIdList。
         */
        Map<String, List<String>> urlMap = new HashMap<>();
        List<String> allApiIdList = new ArrayList<>();
        Map<String, List<String>> caseIdMap = new HashMap<>();
        for (ApiDefinition model : allEffectiveApiList) {
            String url = model.getPath();
            String id = model.getId();
            allApiIdList.add(id);
            if (urlMap.containsKey(url)) {
                urlMap.get(url).add(id);
            } else {
                List<String> list = new ArrayList<>();
                list.add(id);
                urlMap.put(url, list);
            }
        }
        for (ApiTestCase model : allEffectiveApiCaseList) {
            String caseId = model.getId();
            String apiId = model.getApiDefinitionId();
            if (urlMap.containsKey(caseId)) {
                urlMap.get(caseId).add(apiId);
            } else {
                List<String> list = new ArrayList<>();
                list.add(apiId);
                urlMap.put(caseId, list);
            }
        }

        if (allApiIdList.isEmpty()) {
            return 100;
        }

        List<String> urlList = new ArrayList<>();
        List<String> idList = new ArrayList<>();

        for (ApiScenarioWithBLOBs model : allScenarioInfoList) {
            String scenarioDefiniton = model.getScenarioDefinition();
            this.addUrlAndIdToList(scenarioDefiniton, urlList, idList);
        }

        List<String> containsApiIdList = new ArrayList<>();

        for (String url : urlList) {
            List<String> apiIdList = urlMap.get(url);
            if (apiIdList != null) {
                for (String api : apiIdList) {
                    if (!containsApiIdList.contains(api)) {
                        containsApiIdList.add(api);
                    }
                }
            }
        }

        for (String id : idList) {
            List<String> apiIdList = caseIdMap.get(id);
            if (apiIdList != null) {
                for (String api : apiIdList) {
                    if (!containsApiIdList.contains(api)) {
                        containsApiIdList.add(api);
                    }
                }
            }

            if (allApiIdList.contains(id)) {
                if (!containsApiIdList.contains(id)) {
                    containsApiIdList.add(id);
                }
            }
        }

        float coverageRageNumber = (float) containsApiIdList.size() * 100 / allApiIdList.size();
        return coverageRageNumber;
    }

    private void addUrlAndIdToList(String scenarioDefiniton, List<String> urlList, List<String> idList) {
        try {
            JSONObject scenarioObj = JSONObject.parseObject(scenarioDefiniton);
            if (scenarioObj.containsKey("hashTree")) {
                JSONArray hashArr = scenarioObj.getJSONArray("hashTree");
                for (int i = 0; i < hashArr.size(); i++) {
                    JSONObject elementObj = hashArr.getJSONObject(i);
                    if (elementObj.containsKey("id")) {
                        String id = elementObj.getString("id");
                        idList.add(id);
                    }
                    if (elementObj.containsKey("url")) {
                        String url = elementObj.getString("url");
                        urlList.add(url);
                    }
                    if (elementObj.containsKey("path")) {
                        String path = elementObj.getString("path");
                        urlList.add(path);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public ScenarioEnv getApiScenarioProjectId(String id) {
        ApiScenarioWithBLOBs scenario = apiScenarioMapper.selectByPrimaryKey(id);
        ScenarioEnv scenarioEnv = new ScenarioEnv();
        if (scenario == null) {
            return scenarioEnv;
        }

        String definition = scenario.getScenarioDefinition();
        if (StringUtils.isBlank(definition)) {
            return scenarioEnv;
        }

        scenarioEnv = getApiScenarioEnv(definition);
        scenarioEnv.getProjectIds().remove(null);
        scenarioEnv.getProjectIds().add(scenario.getProjectId());
        return scenarioEnv;
    }

    public List<ScenarioIdProjectInfo> getApiScenarioProjectIdByConditions(ApiScenarioBatchRequest request) {
        List<ScenarioIdProjectInfo> returnList = new ArrayList<>();
        if (request.getIds() == null) {
            request.setIds(new ArrayList<>(0));
        }
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));

        if (!request.getIds().isEmpty()) {
            ApiScenarioExample example = new ApiScenarioExample();
            example.createCriteria().andIdIn(request.getIds());
            List<ApiScenarioWithBLOBs> scenarioList = apiScenarioMapper.selectByExampleWithBLOBs(example);
            for (ApiScenarioWithBLOBs scenario : scenarioList) {
                ScenarioEnv scenarioEnv = new ScenarioEnv();
                if (scenario == null) {
                    continue;
                }
                String definition = scenario.getScenarioDefinition();
                if (StringUtils.isBlank(definition)) {
                    continue;
                }
                scenarioEnv = getApiScenarioEnv(definition);
                scenarioEnv.getProjectIds().add(scenario.getProjectId());
                ScenarioIdProjectInfo info = new ScenarioIdProjectInfo();

                info.setProjectIds(scenarioEnv.getProjectIds());
                info.setId(scenario.getId());
                returnList.add(info);
            }
        }
        return returnList;
    }

    public void updateCustomNumByProjectId(String id) {
        extApiScenarioMapper.updateCustomNumByProjectId(id);
    }

    public List<ApiScenarioWithBLOBs> listWithIds(ApiScenarioBatchRequest request) {
        ServiceUtils.getSelectAllIds(request, request.getCondition(),
                (query) -> extApiScenarioMapper.selectIdsByQuery((ApiScenarioRequest) query));
        List<ApiScenarioWithBLOBs> list = extApiScenarioMapper.listWithIds(request.getIds());
        return list;
    }

    public String getLogDetails(String id) {
        ApiScenarioWithBLOBs bloBs = apiScenarioMapper.selectByPrimaryKey(id);
        if (bloBs != null) {
            List<DetailColumn> columns = ReflexObjectUtil.getColumns(bloBs, AutomationReference.automationColumns);
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(id), bloBs.getProjectId(), bloBs.getName(), bloBs.getCreateUser(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }

    public String getLogDetails(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            ApiScenarioExample example = new ApiScenarioExample();
            example.createCriteria().andIdIn(ids);
            List<ApiScenario> definitions = apiScenarioMapper.selectByExample(example);
            List<String> names = definitions.stream().map(ApiScenario::getName).collect(Collectors.toList());
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(ids), definitions.get(0).getProjectId(), String.join(",", names), definitions.get(0).getCreateUser(), new LinkedList<>());
            return JSON.toJSONString(details);
        }
        return null;
    }

    public String getLogDetails(ApiCaseRelevanceRequest request) {
        Map<String, List<String>> mapping = request.getMapping();
        Set<String> set = mapping.keySet();
        if (CollectionUtils.isNotEmpty(set)) {
            ApiScenarioExample example = new ApiScenarioExample();
            example.createCriteria().andIdIn(new ArrayList<>(set));
            List<ApiScenario> scenarios = apiScenarioMapper.selectByExample(example);
            List<String> names = scenarios.stream().map(ApiScenario::getName).collect(Collectors.toList());
            TestPlan testPlan = testPlanMapper.selectByPrimaryKey(request.getPlanId());
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(request.getSelectIds()), testPlan.getProjectId(), String.join(",", names), testPlan.getCreator(), new LinkedList<>());
            return JSON.toJSONString(details);
        }
        return null;
    }

    public void fixUpdate(String projectId) {
        if (!StringUtils.isNotBlank(projectId)) {
            MSException.throwException("参数不允许为空，请填写正确的参数！");
        }
        //根据项目id获取场景信息
        List<ApiScenarioWithBLOBs> apiScenarioInfos = apiScenarioMapper.selectByProjectId(projectId);
        for (int i = 0; i < apiScenarioInfos.size(); i++) {
            String fixScenarioDefinition = apiScenarioInfos.get(i).getScenarioDefinition();
            if (fixScenarioDefinition.contains(BROKEN_CRYPTO_LINE) || fixScenarioDefinition.contains(REDUNDANT_CRYPTO_LINE)) {
                LogUtil.info("待优化场景：" + apiScenarioInfos.get(i).getName());
                LogUtil.info("开始重置场景用例前置脚本！");
                fixScenarioDefinition = fixScenarioDefinition.replaceAll(BROKEN_CRYPTO_LINE, REPLACE_CRYPTO_LINE).replaceAll(REDUNDANT_CRYPTO_LINE, REPLACE_CRYPTO_LINE);
                apiScenarioInfos.get(i).setScenarioDefinition(fixScenarioDefinition);
                //入库
                LogUtil.info("更新数据库场景用例信息！");
                apiScenarioMapper.updateByPrimaryKeyWithBLOBs(apiScenarioInfos.get(i));
            } else {
                continue;
            }
        }
    }
}
