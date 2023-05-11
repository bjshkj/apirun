package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ChartsData;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.jmeter.report.processor.SampleContext;
import org.apache.jmeter.report.processor.graph.impl.CodesPerSecondGraphConsumer;

import java.util.List;

public class ResponseCodeChartReport extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.ResponseCodeChart.name();
    }

    @Override
    public void execute() {
        SampleContext responseCodeMap = sampleContextMap.get(CodesPerSecondGraphConsumer.class.getSimpleName());

        List<ChartsData> resultList = ResultDataParse.graphMapParsing(responseCodeMap.getData(), null, "yAxis");

        saveResult(reportId, resultList);
    }
}
