package io.apirun.performance.parse;

import io.apirun.commons.constants.FileType;
import io.apirun.commons.utils.LogUtil;
import io.apirun.performance.parse.xml.XmlEngineSourceParse;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.InputStream;

public class EngineSourceParserFactory {
    public static final boolean IS_TRANS = false;

    public static EngineSourceParser createEngineSourceParser(String type) {
        final FileType engineType = FileType.valueOf(type);

        if (FileType.JMX.equals(engineType)) {
            return new XmlEngineSourceParse();
        }

        return null;
    }

    public static Document getDocument(InputStream source) throws DocumentException {
        SAXReader reader = new SAXReader();
        try {
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        if (!IS_TRANS) {
            reader.setXMLFilter(EngineSourceParserFactory.getFilter());
        }
        return reader.read(source);
    }

    public static XMLFilterImpl getFilter() {
        return new XMLFilterImpl() {
            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                String text = new String(ch, start, length);

                if (length == 1) {
                    if (StringUtils.equals("&", text)) {
                        char[] escape = "&amp;".toCharArray();
                        super.characters(escape, start, escape.length);
                        return;
                    }
                    if (StringUtils.equals("\"", text)) {
                        char[] escape = "&quot;".toCharArray();
                        super.characters(escape, start, escape.length);
                        return;
                    }
                    if (StringUtils.equals("'", text)) {
                        char[] escape = "&apos;".toCharArray();
                        super.characters(escape, start, escape.length);
                        return;
                    }
                    if (StringUtils.equals("<", text)) {
                        char[] escape = "&lt;".toCharArray();
                        super.characters(escape, start, escape.length);
                        return;
                    }
                    if (StringUtils.equals(">", text)) {
                        char[] escape = "&gt;".toCharArray();
                        super.characters(escape, start, escape.length);
                        return;
                    }
                }

                super.characters(ch, start, length);
            }
        };
    }
}
