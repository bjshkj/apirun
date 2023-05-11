package io.apirun.api.parse.old;

import io.apirun.api.dto.ApiTestImportRequest;
import io.apirun.api.dto.definition.parse.ApiDefinitionImport;
import io.apirun.api.dto.parse.ApiImport;

import java.io.InputStream;

public interface ApiImportParser {
    ApiImport parse(InputStream source, ApiTestImportRequest request);
    ApiDefinitionImport parseApi(InputStream source, ApiTestImportRequest request);

}
