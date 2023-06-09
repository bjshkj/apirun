package io.apirun.performance.parse.xml.reader;

import io.apirun.commons.constants.FileType;
import io.apirun.performance.parse.xml.reader.jmx.JmeterDocumentParser;

public class DocumentParserFactory {
    public static DocumentParser createDocumentParser(String type) {
        final FileType fileType = FileType.valueOf(type);

        switch (fileType) {
            case JMX:
                return new JmeterDocumentParser();
            default:
                break;
        }
        return null;
    }
}
