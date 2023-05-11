package io.apirun.performance.parse;

import io.apirun.performance.engine.EngineContext;

import java.io.InputStream;

public interface EngineSourceParser {
    String parse(EngineContext context, InputStream source) throws Exception;
}
