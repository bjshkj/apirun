package io.apirun.api.dto.definition.parse;

import io.apirun.api.parse.ApiImportParser;
import io.apirun.commons.constants.ApiImportPlatform;
import org.apache.commons.lang3.StringUtils;

public class ApiDefinitionImportParserFactory {
    public static ApiImportParser getApiImportParser(String platform) {
        if (StringUtils.equals(ApiImportPlatform.Metersphere.name(), platform)) {
            return new MsDefinitionParser();
        } else if (StringUtils.equals(ApiImportPlatform.Postman.name(), platform)) {
            return new PostmanDefinitionParser();
        } else if (StringUtils.equals(ApiImportPlatform.Swagger2.name(), platform)) {
            return new Swagger2Parser();
        }else if (StringUtils.equals(ApiImportPlatform.Har.name(), platform)) {
            return new HarParser();
        }else if (StringUtils.equals(ApiImportPlatform.ESB.name(), platform)) {
            return new ESBParser();
        }
        return null;
    }
}
