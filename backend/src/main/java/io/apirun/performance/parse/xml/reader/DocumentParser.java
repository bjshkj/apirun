package io.apirun.performance.parse.xml.reader;

import io.apirun.performance.engine.EngineContext;
import org.w3c.dom.Document;

public interface DocumentParser {
    String parse(EngineContext context, Document document) throws Exception;
}
