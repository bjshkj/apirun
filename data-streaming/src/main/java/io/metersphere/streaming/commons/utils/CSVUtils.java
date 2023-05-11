package io.metersphere.streaming.commons.utils;

import org.apache.jmeter.save.CSVSaveService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class CSVUtils {

    public static final char DEFAULT_SEPARATOR = ',';

    public static String[] parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR);
    }

    public static String[] parseLine(String line, char separators) {
        StringReader stringReader = new StringReader(line);
        try {
            return CSVSaveService.csvReadFile(new BufferedReader(stringReader), separators);
        } catch (IOException e) {
            LogUtil.error(e);
        }
        return new String[0];
    }
}