package io.apirun.api.parse;

import io.apirun.api.dto.ApiTestImportRequest;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;

public interface ApiImportParser<T> {
    T parse(InputStream source, ApiTestImportRequest request) throws IOException, InvalidFormatException, Exception;
}
