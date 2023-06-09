package io.apirun.api.dto.definition.parse;


import io.apirun.api.parse.ApiImportAbstractParser;
import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.base.domain.ApiModule;

import java.util.List;

/**
 * @author song.tianyang
 * @Date 2021/3/10 11:15 上午
 * @Description
 */
public abstract class EsbAbstractParser extends ApiImportAbstractParser<ApiDefinitionImport> {

    protected void buildModule(ApiModule parentModule, ApiDefinitionWithBLOBs apiDefinition, List<String> tags) {
        if (tags != null) {
            tags.forEach(tag -> {
                ApiModule module = ApiDefinitionImportUtil.buildModule(parentModule, tag, this.projectId);
                apiDefinition.setModuleId(module.getId());
            });
        }else {
            apiDefinition.setModuleId(parentModule.getId());
        }
    }

}
