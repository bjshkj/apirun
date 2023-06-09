package io.apirun.api.dto.definition.parse;

import io.apirun.api.dto.definition.ApiModuleDTO;
import io.apirun.api.dto.scenario.request.RequestType;
import io.apirun.api.service.ApiModuleService;
import io.apirun.base.domain.ApiModule;
import io.apirun.commons.utils.BeanUtils;
import io.apirun.commons.utils.CommonBeanFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ApiDefinitionImportUtil {

    public static ApiModule getSelectModule(String moduleId) {
        ApiModuleService apiModuleService = CommonBeanFactory.getBean(ApiModuleService.class);
        if (StringUtils.isNotBlank(moduleId) && !StringUtils.equals("root", moduleId)) {
            ApiModule module = new ApiModule();
            ApiModuleDTO moduleDTO = apiModuleService.getNode(moduleId);
            if (moduleDTO != null) {
                BeanUtils.copyBean(module, moduleDTO);
            }
            return module;
        }
        return null;
    }

    public static String getSelectModulePath(String path, String pid) {
        ApiModuleService apiModuleService = CommonBeanFactory.getBean(ApiModuleService.class);
        if (StringUtils.isNotBlank(pid)) {
            ApiModuleDTO moduleDTO = apiModuleService.getNode(pid);
            if (moduleDTO != null) {
                return getSelectModulePath(moduleDTO.getName() + "/" + path, moduleDTO.getParentId());
            }
        }
        return "/" + path;
    }

    public static ApiModule getNodeTree(String projectId) {
        ApiModuleService apiModuleService = CommonBeanFactory.getBean(ApiModuleService.class);
        List<ApiModuleDTO> nodeTrees = apiModuleService.getNodeTreeByProjectId(projectId, RequestType.HTTP);

        return null;
    }

    public static ApiModule buildModule(ApiModule parentModule, String name, String projectId) {
        ApiModuleService apiModuleService = CommonBeanFactory.getBean(ApiModuleService.class);
        ApiModule module;
        if (parentModule != null) {
            module = apiModuleService.getNewModule(name, projectId, parentModule.getLevel() + 1);
            module.setParentId(parentModule.getId());
        } else {
            module = apiModuleService.getNewModule(name, projectId, 1);
        }
        createModule(module);
        return module;
    }

    public static void createModule(ApiModule module) {
        ApiModuleService apiModuleService = CommonBeanFactory.getBean(ApiModuleService.class);
        module.setProtocol(RequestType.HTTP);
        if (module.getName().length() > 64) {
            module.setName(module.getName().substring(0, 64));
        }
        List<ApiModule> apiModules = apiModuleService.selectSameModule(module);
        if (CollectionUtils.isEmpty(apiModules)) {
            apiModuleService.addNode(module);
        } else {
            module.setId(apiModules.get(0).getId());
        }
    }
}
