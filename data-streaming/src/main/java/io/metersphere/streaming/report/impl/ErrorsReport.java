package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.Errors;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.jmeter.report.processor.ErrorsSummaryConsumer;
import org.apache.jmeter.report.processor.SampleContext;

import java.math.BigDecimal;
import java.util.List;

public class ErrorsReport extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.Errors.name();
    }

    @Override
    public void execute() {
        SampleContext sampleContext = sampleContextMap.get(ErrorsSummaryConsumer.class.getSimpleName());
        List<Errors> errors = ResultDataParse.summaryMapParsing(sampleContext.getData(), Errors.class);
        errors.forEach(e -> {
            e.setPercentOfErrors(format2.format(new BigDecimal(e.getPercentOfErrors())));
            e.setPercentOfAllSamples(format2.format(new BigDecimal(e.getPercentOfAllSamples())));
        });
        saveResult(reportId, errors);
    }
}
