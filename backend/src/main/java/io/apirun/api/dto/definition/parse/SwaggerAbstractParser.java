package io.apirun.api.dto.definition.parse;

import io.apirun.api.parse.ApiImportAbstractParser;
import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.base.domain.ApiModule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class SwaggerAbstractParser extends ApiImportAbstractParser<ApiDefinitionImport> {

    protected void buildModule(ApiModule parentModule, ApiDefinitionWithBLOBs apiDefinition,
                               List<String> tags, String selectModulePath) {
        if (CollectionUtils.isEmpty(tags)){
            if (parentModule != null) {
                apiDefinition.setModuleId(parentModule.getId());
                apiDefinition.setModulePath(selectModulePath);
            }
        }else {
            tags.forEach(tag -> {
                ApiModule module = ApiDefinitionImportUtil.buildModule(parentModule, tag, this.projectId);
                apiDefinition.setModuleId(module.getId());
                if (StringUtils.isNotBlank(selectModulePath)) {
                    apiDefinition.setModulePath(selectModulePath + "/" + tag);
                } else {
                    apiDefinition.setModulePath("/" + tag);
                }
            });
        }
    }

}
