package io.apirun.api.dto.automation.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import io.apirun.api.dto.ApiTestImportRequest;
import io.apirun.api.dto.definition.request.MsScenario;
import io.apirun.api.dto.definition.request.MsTestElement;
import io.apirun.api.parse.MsAbstractParser;
import io.apirun.base.domain.ApiScenarioModule;
import io.apirun.base.domain.ApiScenarioWithBLOBs;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.*;

public class MsScenarioParser extends MsAbstractParser<ScenarioImport> {

    private ApiScenarioModule selectModule;

    private String selectModulePath;

    @Override
    public ScenarioImport parse(InputStream source, ApiTestImportRequest request) {
        String testStr = getApiTestStr(source);
        this.projectId = request.getProjectId();
        JSONObject testObject = JSONObject.parseObject(testStr, Feature.OrderedField);

        if (StringUtils.isNotBlank(request.getModuleId())) {
            this.selectModule = ApiScenarioImportUtil.getSelectModule(request.getModuleId());
            if (this.selectModule != null) {
                this.selectModulePath = ApiScenarioImportUtil.getSelectModulePath(this.selectModule.getName(), this.selectModule.getParentId());
            }
        }

        if (testObject.get("projectName") != null || testObject.get("projectId") != null ) {
            return parseMsFormat(testStr, request);
        } else {
            ScenarioImport apiImport = new ScenarioImport();
            ArrayList<ApiScenarioWithBLOBs> apiScenarioWithBLOBs = new ArrayList<>();
            apiScenarioWithBLOBs.add(parsePluginFormat(testObject, request));
            apiImport.setData(apiScenarioWithBLOBs);
            return apiImport;
        }
    }

    protected ApiScenarioWithBLOBs parsePluginFormat(JSONObject testObject, ApiTestImportRequest importRequest) {
        LinkedList<MsTestElement> results = new LinkedList<>();
        testObject.keySet().forEach(tag -> {
            results.addAll(parseMsHTTPSamplerProxy(testObject, tag));
        });
        MsScenario msScenario = new MsScenario();
        msScenario.setName(importRequest.getFileName());
        msScenario.setHashTree(results);
        ApiScenarioWithBLOBs scenarioWithBLOBs = parseScenario(msScenario);
        scenarioWithBLOBs.setApiScenarioModuleId(importRequest.getModuleId());
        return scenarioWithBLOBs;
    }

    private ScenarioImport parseMsFormat(String testStr, ApiTestImportRequest importRequest) {
        ScenarioImport apiDefinitionImport = JSON.parseObject(testStr, ScenarioImport.class);
        List<ApiScenarioWithBLOBs> data = apiDefinitionImport.getData();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(item -> {
                String scenarioDefinitionStr = item.getScenarioDefinition();
                if (StringUtils.isNotBlank(scenarioDefinitionStr)) {
                    JSONObject scenarioDefinition = JSONObject.parseObject(scenarioDefinitionStr);
                    if (scenarioDefinition != null) {
                        JSONArray hashTree = scenarioDefinition.getJSONArray("hashTree");
                        setCopy(hashTree);
                        item.setScenarioDefinition(JSONObject.toJSONString(scenarioDefinition));
                    }
                }
                if (StringUtils.isBlank(item.getModulePath())) {
                    item.setApiScenarioModuleId(null);
                }
                parseModule(item.getModulePath(), importRequest, item);
                item.setId(UUID.randomUUID().toString());
                item.setProjectId(this.projectId);
            });
        }
        return apiDefinitionImport;
    }

    private void setCopy(JSONArray hashTree) {
        // 将引用转成复制
        if (CollectionUtils.isNotEmpty(hashTree)) {
            for (int i = 0; i < hashTree.size(); i++) {
                JSONObject object = (JSONObject) hashTree.get(i);
                String referenced = object.getString("referenced");
                if (StringUtils.isNotBlank(referenced) && StringUtils.equals(referenced, "REF")) {
                    object.put("referenced", "Copy");
                }
                if (CollectionUtils.isNotEmpty(object.getJSONArray("hashTree"))) {
                    setCopy(object.getJSONArray("hashTree"));
                }
            }
        }
    }

    protected void parseModule(String modulePath, ApiTestImportRequest importRequest, ApiScenarioWithBLOBs apiScenarioWithBLOBs) {
        if (StringUtils.isEmpty(modulePath)) {
            return;
        }
        if (modulePath.startsWith("/")) {
            modulePath = modulePath.substring(1, modulePath.length());
        }
        if (modulePath.endsWith("/")) {
            modulePath = modulePath.substring(0, modulePath.length() - 1);
        }
        List<String> modules = Arrays.asList(modulePath.split("/"));
        ApiScenarioModule parent = this.selectModule;
        Iterator<String> iterator = modules.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            parent = ApiScenarioImportUtil.buildModule(parent, item, this.projectId);
            if (!iterator.hasNext()) {
                apiScenarioWithBLOBs.setApiScenarioModuleId(parent.getId());
                String path = apiScenarioWithBLOBs.getModulePath() == null ? "" : apiScenarioWithBLOBs.getModulePath();
                if (StringUtils.isNotBlank(this.selectModulePath)) {
                    apiScenarioWithBLOBs.setModulePath(this.selectModulePath + path);
                } else if (StringUtils.isBlank(importRequest.getModuleId())) {
                    apiScenarioWithBLOBs.setModulePath("/默认模块" + path);
                }
            }
        }
    }
}
