package io.apirun.api.dto.definition.parse;

import com.alibaba.fastjson.JSON;
import io.apirun.api.dto.ApiTestImportRequest;
import io.apirun.api.dto.definition.request.sampler.MsHTTPSamplerProxy;
import io.apirun.api.dto.parse.postman.PostmanCollection;
import io.apirun.api.dto.parse.postman.PostmanItem;
import io.apirun.api.dto.parse.postman.PostmanKeyValue;
import io.apirun.api.parse.PostmanAbstractParserParser;
import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.base.domain.ApiModule;
import org.apache.commons.lang3.StringUtils;
import io.apirun.base.domain.ApiTestCaseWithBLOBs;
import io.apirun.base.domain.Project;
import io.apirun.base.mapper.ProjectMapper;
import io.apirun.commons.utils.BeanUtils;
import io.apirun.commons.utils.CommonBeanFactory;

import java.io.InputStream;
import java.util.*;

public class PostmanDefinitionParser extends PostmanAbstractParserParser<ApiDefinitionImport> {

    private ApiModule selectModule;

    private String selectModulePath;

    @Override
    public ApiDefinitionImport parse(InputStream source, ApiTestImportRequest request) {
        String testStr = getApiTestStr(source);
        this.projectId = request.getProjectId();
        PostmanCollection postmanCollection = JSON.parseObject(testStr, PostmanCollection.class);
        List<PostmanKeyValue> variables = postmanCollection.getVariable();
        ApiDefinitionImport apiImport = new ApiDefinitionImport();
        List<ApiDefinitionWithBLOBs> results = new ArrayList<>();
        this.selectModule = ApiDefinitionImportUtil.getSelectModule(request.getModuleId());
        if (this.selectModule != null) {
            this.selectModulePath = ApiDefinitionImportUtil.getSelectModulePath(this.selectModule.getName(), this.selectModule.getParentId());
        }

        ApiModule apiModule = ApiDefinitionImportUtil.buildModule(this.selectModule, postmanCollection.getInfo().getName(), this.projectId);
        List<ApiTestCaseWithBLOBs> cases = new ArrayList<>();
        Map<String, String> repeatMap = new HashMap();
        ProjectMapper projectMapper = CommonBeanFactory.getBean(ProjectMapper.class);
        Project project = projectMapper.selectByPrimaryKey(request.getProjectId());
        parseItem(postmanCollection.getItem(), variables, results,
                apiModule, apiModule.getName(), cases, repeatMap, project.getRepeatable());
        Collections.reverse(results); // 调整顺序
        Collections.reverse(cases);
        apiImport.setData(results);
        apiImport.setCases(cases);
        return apiImport;
    }

    protected void parseItem(List<PostmanItem> items, List<PostmanKeyValue> variables, List<ApiDefinitionWithBLOBs> results,
                             ApiModule parentModule, String path, List<ApiTestCaseWithBLOBs> cases, Map<String, String> repeatMap, Boolean repeatable) {
        for (PostmanItem item : items) {
            List<PostmanItem> childItems = item.getItem();
            if (childItems != null) {
                ApiModule module = null;
                module = ApiDefinitionImportUtil.buildModule(parentModule, item.getName(), this.projectId);
                parseItem(childItems, variables, results, module,  path + "/" + module.getName(), cases, repeatMap, repeatable);
            } else {
                MsHTTPSamplerProxy msHTTPSamplerProxy = parsePostman(item);
                ApiDefinitionWithBLOBs request = buildApiDefinition(msHTTPSamplerProxy.getId(), msHTTPSamplerProxy.getName(),
                        msHTTPSamplerProxy.getPath(), msHTTPSamplerProxy.getMethod(), new ApiTestImportRequest());
                request.setPath(msHTTPSamplerProxy.getPath());
                request.setRequest(JSON.toJSONString(msHTTPSamplerProxy));
                if (parentModule != null) {
                    request.setModuleId(parentModule.getId());
                    if (StringUtils.isNotBlank(this.selectModulePath)) {
                        request.setModulePath(this.selectModulePath + "/" + path);
                    } else {
                        request.setModulePath("/" + path);
                    }
                }
                if (request != null) {
                    if (repeatMap.keySet().contains(request.getMethod() + request.getPath())
                            && (repeatable == null || repeatable == false)) {
                        ApiTestCaseWithBLOBs apiTestCase =  new ApiTestCaseWithBLOBs();
                        BeanUtils.copyBean(apiTestCase, request);
                        apiTestCase.setApiDefinitionId(repeatMap.get(request.getMethod() + request.getPath()));
                        apiTestCase.setPriority("P0");
                        cases.add(apiTestCase);
                    } else {
                        repeatMap.put(request.getMethod() + request.getPath(), request.getId());
                        results.add(request);
                    }
                }
            }
        }
    }
}
