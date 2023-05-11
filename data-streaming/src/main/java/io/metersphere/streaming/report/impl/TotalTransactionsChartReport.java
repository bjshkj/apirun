package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ChartsData;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.jmeter.report.processor.SampleContext;
import org.apache.jmeter.report.processor.graph.impl.TotalTPSGraphConsumer;

import java.util.List;

public class TotalTransactionsChartReport extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.TotalTransactionsChart.name();
    }

    @Override
    public void execute() {
        SampleContext responseTimeMap = sampleContextMap.get(TotalTPSGraphConsumer.class.getSimpleName());

        List<ChartsData> resultList = ResultDataParse.graphMapParsing(responseTimeMap.getData(), "", "yAxis");

        saveResult(reportId, resultList);
    }
}
