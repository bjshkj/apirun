package io.apirun.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.apirun.api.dto.ApiTestEnvironmentDTO;
import io.apirun.api.dto.mockconfig.MockConfigStaticData;
import io.apirun.base.domain.ApiTestEnvironmentExample;
import io.apirun.base.domain.ApiTestEnvironmentWithBLOBs;
import io.apirun.base.mapper.ApiTestEnvironmentMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.CommonBeanFactory;
import io.apirun.commons.utils.FileUtils;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.controller.request.EnvironmentRequest;
import io.apirun.dto.BaseSystemConfigDTO;
import io.apirun.i18n.Translator;
import io.apirun.log.utils.ReflexObjectUtil;
import io.apirun.log.vo.DetailColumn;
import io.apirun.log.vo.OperatingLogDetails;
import io.apirun.log.vo.system.SystemReference;
import io.apirun.service.SystemParameterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApiTestEnvironmentService {

    @Resource
    private ApiTestEnvironmentMapper apiTestEnvironmentMapper;

    public List<ApiTestEnvironmentWithBLOBs> list(String projectId) {
        ApiTestEnvironmentExample example = new ApiTestEnvironmentExample();
        example.createCriteria().andProjectIdEqualTo(projectId);
        return apiTestEnvironmentMapper.selectByExampleWithBLOBs(example);
    }

    public List<ApiTestEnvironmentWithBLOBs> listByConditions(EnvironmentRequest environmentRequest) {
        if (CollectionUtils.isEmpty(environmentRequest.getProjectIds())) {
            return new ArrayList<>();
        }
        ApiTestEnvironmentExample example = new ApiTestEnvironmentExample();
        ApiTestEnvironmentExample.Criteria criteria = example.createCriteria();
        criteria.andProjectIdIn(environmentRequest.getProjectIds());
        if (StringUtils.isNotBlank(environmentRequest.getName())) {
            environmentRequest.setName(StringUtils.wrapIfMissing(environmentRequest.getName(), '%'));    //使搜索文本变成数据库中的正则表达式
            criteria.andNameLike(environmentRequest.getName());
        }
        return apiTestEnvironmentMapper.selectByExampleWithBLOBs(example);
    }

    public List<ApiTestEnvironmentWithBLOBs> selectByExampleWithBLOBs(ApiTestEnvironmentExample example) {
        return apiTestEnvironmentMapper.selectByExampleWithBLOBs(example);
    }


    public ApiTestEnvironmentWithBLOBs get(String id) {
        return apiTestEnvironmentMapper.selectByPrimaryKey(id);
    }

    public void delete(String id) {
        apiTestEnvironmentMapper.deleteByPrimaryKey(id);
    }

    public void update(ApiTestEnvironmentWithBLOBs apiTestEnvironment) {
        checkEnvironmentExist(apiTestEnvironment);
        apiTestEnvironmentMapper.updateByPrimaryKeyWithBLOBs(apiTestEnvironment);
    }

    public String add(ApiTestEnvironmentWithBLOBs apiTestEnvironmentWithBLOBs) {
        apiTestEnvironmentWithBLOBs.setId(UUID.randomUUID().toString());
        checkEnvironmentExist(apiTestEnvironmentWithBLOBs);
        apiTestEnvironmentMapper.insert(apiTestEnvironmentWithBLOBs);
        return apiTestEnvironmentWithBLOBs.getId();
    }

    public String add(ApiTestEnvironmentDTO request, List<MultipartFile> sslFiles) {
        request.setId(UUID.randomUUID().toString());
        request.setCreateUser(SessionUtils.getUserId());
        checkEnvironmentExist(request);
        FileUtils.createFiles(request.getUploadIds(), sslFiles, FileUtils.BODY_FILE_DIR + "/ssl");
        //检查Config，判断isMock参数是否给True
        request = this.updateConfig(request,false);
        apiTestEnvironmentMapper.insert(request);
        return request.getId();
    }

    private ApiTestEnvironmentDTO updateConfig(ApiTestEnvironmentDTO request, boolean isMock) {
        if(StringUtils.isNotEmpty(request.getConfig())){
            try{
                JSONObject configObj = JSONObject.parseObject(request.getConfig());
                if(configObj.containsKey("httpConfig")){
                    JSONObject httpObj = configObj.getJSONObject("httpConfig");
                    httpObj.put("isMock",isMock);
                }
                request.setConfig(configObj.toJSONString());
            }catch (Exception e){
            }
        }
        return request;
    }

    public void update(ApiTestEnvironmentDTO apiTestEnvironment,List<MultipartFile> sslFiles) {
        checkEnvironmentExist(apiTestEnvironment);
        FileUtils.createFiles(apiTestEnvironment.getUploadIds(), sslFiles, FileUtils.BODY_FILE_DIR + "/ssl");
        apiTestEnvironmentMapper.updateByPrimaryKeyWithBLOBs(apiTestEnvironment);
    }
    private void checkEnvironmentExist(ApiTestEnvironmentWithBLOBs environment) {
        if (environment.getName() != null) {
            ApiTestEnvironmentExample example = new ApiTestEnvironmentExample();
            ApiTestEnvironmentExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(environment.getName())
                    .andProjectIdEqualTo(environment.getProjectId());
            if (StringUtils.isNotBlank(environment.getId())) {
                criteria.andIdNotEqualTo(environment.getId());
            }
            if (apiTestEnvironmentMapper.selectByExample(example).size() > 0) {
                MSException.throwException(Translator.get("api_test_environment_already_exists"));
            }
        }
    }

    /**
     * 通过项目ID获取Mock环境  （暂时定义mock环境为： name = Mock环境）
     *
     * @param projectId
     * @return
     */
    public synchronized ApiTestEnvironmentWithBLOBs getMockEnvironmentByProjectId(String projectId, String protocal, String baseUrl) {
        //创建的时候检查当前站点
        SystemParameterService systemParameterService = CommonBeanFactory.getBean(SystemParameterService.class);
        BaseSystemConfigDTO baseSystemConfigDTO = systemParameterService.getBaseInfo();
        if (baseSystemConfigDTO != null && StringUtils.isNotEmpty(baseSystemConfigDTO.getUrl())) {
            baseUrl = baseSystemConfigDTO.getUrl();
            if (baseUrl.startsWith("http:")) {
                protocal = "http";
            } else if (baseUrl.startsWith("https:")) {
                protocal = "https";
            }
        }

        String apiName = MockConfigStaticData.MOCK_EVN_NAME;
        ApiTestEnvironmentWithBLOBs returnModel = null;
        ApiTestEnvironmentExample example = new ApiTestEnvironmentExample();
        example.createCriteria().andProjectIdEqualTo(projectId).andNameEqualTo(apiName);
        List<ApiTestEnvironmentWithBLOBs> list = this.selectByExampleWithBLOBs(example);
        if (list.isEmpty()) {
            returnModel = this.genHttpApiTestEnvironmentByUrl(projectId, protocal, apiName, baseUrl);
            this.add(returnModel);
        } else {
            returnModel = list.get(0);
            returnModel = this.checkMockEvnIsRightful(returnModel, protocal, projectId, apiName, baseUrl);
        }
        return returnModel;
    }

    private ApiTestEnvironmentWithBLOBs checkMockEvnIsRightful(ApiTestEnvironmentWithBLOBs returnModel, String protocal, String projectId, String name, String url) {
        boolean needUpdate = false;
        if (returnModel.getConfig() != null) {
            try {
                JSONObject configObj = JSONObject.parseObject(returnModel.getConfig());
                if (configObj.containsKey("httpConfig")) {
                    JSONObject httpObj = configObj.getJSONObject("httpConfig");
                    if (httpObj.containsKey("isMock") && httpObj.getBoolean("isMock")) {
                        if (httpObj.containsKey("conditions")) {
                            JSONArray conditions = httpObj.getJSONArray("conditions");
                            if (conditions.isEmpty()) {
                                needUpdate = true;
                            } else {
                                for (int i = 0; i < conditions.size(); i++) {
                                    JSONObject obj = conditions.getJSONObject(i);
                                    String socket = url;
                                    if (socket.startsWith("http://")) {
                                        socket = socket.substring(7);
                                    } else if (socket.startsWith("https://")) {
                                        socket = socket.substring(8);
                                    }
                                    if (!obj.containsKey("socket") || !StringUtils.equals(socket, String.valueOf(obj.get("socket")))) {
                                        needUpdate = true;
                                    } else if (!obj.containsKey("protocol") || !StringUtils.equals(protocal, String.valueOf(obj.get("protocol")))) {
                                        needUpdate = true;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (needUpdate) {
            String id = returnModel.getId();
            returnModel = this.genHttpApiTestEnvironmentByUrl(projectId, protocal, name, url);
            returnModel.setId(id);
            apiTestEnvironmentMapper.updateByPrimaryKeyWithBLOBs(returnModel);
        }
        return returnModel;
    }

    private ApiTestEnvironmentWithBLOBs genHttpApiTestEnvironmentByUrl(String projectId, String protocal, String name, String url) {
        String socket = "";
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }
        socket = url;

        String portStr = "";
        String ipStr = url;
        if (url.contains(":") && !url.endsWith(":")) {
            String[] urlArr = url.split(":");
            int port = -1;
            try {
                port = Integer.parseInt(urlArr[urlArr.length - 1]);
            } catch (Exception e) {
            }
            if (port > -1) {
                portStr = String.valueOf(port);
                ipStr = urlArr[0];
            }
        }

        JSONObject commonConfigObj = new JSONObject();
        JSONArray commonVariablesArr = new JSONArray();
        Map<String, Object> commonMap = new HashMap<>();
        commonMap.put("enable", true);
        commonVariablesArr.add(commonMap);
        commonConfigObj.put("variables", commonVariablesArr);
        commonConfigObj.put("enableHost", false);
        commonConfigObj.put("hosts", new String[]{});

        JSONObject httpConfig = new JSONObject();
        httpConfig.put("socket", null);
        httpConfig.put("isMock", true);
        httpConfig.put("domain", null);
        JSONArray httpVariablesArr = new JSONArray();
        Map<String, Object> httpMap = new HashMap<>();
        httpMap.put("enable", true);
        httpVariablesArr.add(httpMap);
        httpConfig.put("headers", new JSONArray(httpVariablesArr));
        httpConfig.put("protocol", null);
        httpConfig.put("port", null);
        JSONArray httpItemArr = new JSONArray();
        JSONObject httpItem = new JSONObject();
        httpItem.put("id", UUID.randomUUID().toString());
        httpItem.put("type", "NONE");
        httpItem.put("socket", socket);
        httpItem.put("protocol", protocal);
        JSONArray protocolVariablesArr = new JSONArray();
        Map<String, Object> protocolMap = new HashMap<>();
        protocolMap.put("enable", true);
        protocolVariablesArr.add(protocolMap);
        httpItem.put("headers", new JSONArray(protocolVariablesArr));
        httpItem.put("domain", ipStr);
        if (StringUtils.isNotEmpty(portStr)) {
            httpItem.put("port", portStr);
        } else {
            httpItem.put("port", "");
        }
        JSONArray detailArr = new JSONArray();
        JSONObject detailObj = new JSONObject();
        detailObj.put("name", "");
        detailObj.put("value", "contains");
        detailObj.put("enable", true);
        detailArr.add(detailObj);
        httpItem.put("details", detailArr);

        httpItemArr.add(httpItem);
        httpConfig.put("conditions", httpItemArr);
        httpConfig.put("defaultCondition", "NONE");

        JSONArray databaseConfigObj = new JSONArray();

        JSONObject tcpConfigObj = new JSONObject();
        tcpConfigObj.put("classname", "TCPClientImpl");
        tcpConfigObj.put("reUseConnection", false);
        tcpConfigObj.put("nodelay", false);
        tcpConfigObj.put("closeConnection", false);

        JSONObject object = new JSONObject();
        object.put("commonConfig", commonConfigObj);
        object.put("httpConfig", httpConfig);
        object.put("databaseConfigs", databaseConfigObj);
        object.put("tcpConfig", tcpConfigObj);

        ApiTestEnvironmentWithBLOBs blobs = new ApiTestEnvironmentWithBLOBs();
        blobs.setProjectId(projectId);
        blobs.setName(name);
        blobs.setConfig(object.toString());

        return blobs;
    }

    public void checkMockEvnInfoByBaseUrl(String baseUrl) {
        List<ApiTestEnvironmentWithBLOBs> allEvnList = this.selectByExampleWithBLOBs(null);
        for (ApiTestEnvironmentWithBLOBs model : allEvnList) {
            if (StringUtils.equals(model.getName(), MockConfigStaticData.MOCK_EVN_NAME)) {
                String protocal = "";
                if (baseUrl.startsWith("http:")) {
                    protocal = "http";
                } else if (baseUrl.startsWith("https:")) {
                    protocal = "https";
                }

                model = this.checkMockEvnIsRightful(model, protocal, model.getProjectId(), model.getName(), baseUrl);
            }
        }
    }

    public String getLogDetails(String id) {
        ApiTestEnvironmentWithBLOBs bloBs = apiTestEnvironmentMapper.selectByPrimaryKey(id);
        if (bloBs != null) {
            List<DetailColumn> columns = ReflexObjectUtil.getColumns(bloBs, SystemReference.environmentColumns);
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(bloBs.getId()), bloBs.getProjectId(), bloBs.getName(), bloBs.getCreateUser(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }
}
