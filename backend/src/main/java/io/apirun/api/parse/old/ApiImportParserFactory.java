package io.apirun.api.parse.old;

import io.apirun.commons.constants.ApiImportPlatform;
import org.apache.commons.lang3.StringUtils;

public class ApiImportParserFactory {
    public static ApiImportParser getApiImportParser(String platform) {
        if (StringUtils.equals(ApiImportPlatform.Metersphere.name(), platform)) {
            return new MsParser();
        } else if (StringUtils.equals(ApiImportPlatform.Postman.name(), platform)) {
            return new PostmanParser();
        } else if (StringUtils.equals(ApiImportPlatform.Swagger2.name(), platform)) {
            return new Swagger2Parser();
        }
        return null;
    }
}
