package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ChartsData;
import io.metersphere.streaming.report.graph.consumer.ErrorsGraphConsumer;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.jmeter.report.processor.SampleContext;

import java.util.List;

public class ErrorsChartReport extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.ErrorsChart.name();
    }

    @Override
    public void execute() {
        SampleContext responseCodeMap = sampleContextMap.get(ErrorsGraphConsumer.class.getSimpleName());

        List<ChartsData> resultList = ResultDataParse.graphMapParsing(responseCodeMap.getData(), null, "yAxis");

        saveResult(reportId, resultList);
    }
}
