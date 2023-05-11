package io.apirun.api.dto.automation.parse;

import io.apirun.api.dto.automation.ApiScenarioModuleDTO;
import io.apirun.api.service.ApiScenarioModuleService;
import io.apirun.base.domain.ApiScenarioModule;
import io.apirun.commons.utils.BeanUtils;
import io.apirun.commons.utils.CommonBeanFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ApiScenarioImportUtil {

    public static ApiScenarioModule getSelectModule(String moduleId) {
        ApiScenarioModuleService apiScenarioModuleService = CommonBeanFactory.getBean(ApiScenarioModuleService.class);
        if (StringUtils.isNotBlank(moduleId) && !StringUtils.equals("root", moduleId)) {
            ApiScenarioModule module = new ApiScenarioModule();
            ApiScenarioModuleDTO moduleDTO = apiScenarioModuleService.getNode(moduleId);
            if (moduleDTO != null) {
                BeanUtils.copyBean(module, moduleDTO);
            }
            return module;
        }
        return null;
    }

    public static String getSelectModulePath(String path, String pid) {
        ApiScenarioModuleService apiScenarioModuleService = CommonBeanFactory.getBean(ApiScenarioModuleService.class);
        if (StringUtils.isNotBlank(pid)) {
            ApiScenarioModuleDTO moduleDTO = apiScenarioModuleService.getNode(pid);
            if (moduleDTO != null) {
                return getSelectModulePath(moduleDTO.getName() + "/" + path, moduleDTO.getParentId());
            }
        }
        return "/" + path;
    }

    public static ApiScenarioModule buildModule(ApiScenarioModule parentModule, String name, String projectId) {
        ApiScenarioModuleService apiModuleService = CommonBeanFactory.getBean(ApiScenarioModuleService.class);
        ApiScenarioModule module;
        if (parentModule != null) {
            module = apiModuleService.getNewModule(name, projectId, parentModule.getLevel() + 1);
            module.setParentId(parentModule.getId());
        } else {
            module = apiModuleService.getNewModule(name, projectId, 1);
        }
        createModule(module);
        return module;
    }

    public static void createModule(ApiScenarioModule module) {
        ApiScenarioModuleService apiModuleService = CommonBeanFactory.getBean(ApiScenarioModuleService.class);
        if (module.getName().length() > 64) {
            module.setName(module.getName().substring(0, 64));
        }
        List<ApiScenarioModule> apiModules = apiModuleService.selectSameModule(module);
        if (CollectionUtils.isEmpty(apiModules)) {
            apiModuleService.addNode(module);
        } else {
            module.setId(apiModules.get(0).getId());
        }
    }
}
