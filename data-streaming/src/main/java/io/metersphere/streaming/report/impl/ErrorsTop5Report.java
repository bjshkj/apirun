package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ErrorsTop5;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.jmeter.report.processor.SampleContext;
import org.apache.jmeter.report.processor.Top5ErrorsBySamplerConsumer;

import java.util.List;

public class ErrorsTop5Report extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.ErrorsTop5.name();
    }

    @Override
    public void execute() {
        SampleContext sampleContext = sampleContextMap.get(Top5ErrorsBySamplerConsumer.class.getSimpleName());
        List<ErrorsTop5> errorsTop5s = ResultDataParse.summaryMapParsing(sampleContext.getData(), ErrorsTop5.class);

        saveResult(reportId, errorsTop5s);
    }
}
