package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ChartsData;
import io.metersphere.streaming.report.graph.consumer.DistributedActiveThreadsGraphConsumer;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.jmeter.report.processor.SampleContext;
import org.apache.jmeter.report.processor.graph.impl.TransactionsPerSecondGraphConsumer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class LoadChartReport extends AbstractReport {


    @Override
    public void execute() {
        SampleContext activeThreadMap = sampleContextMap.get(DistributedActiveThreadsGraphConsumer.class.getSimpleName());
        SampleContext hitsMap = sampleContextMap.get(TransactionsPerSecondGraphConsumer.class.getSimpleName());
        List<ChartsData> resultList = ResultDataParse.graphMapParsing(activeThreadMap.getData(), "", "yAxis");
        // 使用整数来统计线程数
        resultList.forEach(cs -> {
            DecimalFormat decimalFormat = new DecimalFormat("0");
            cs.setyAxis(new BigDecimal(decimalFormat.format(cs.getyAxis())));
        });
        List<ChartsData> hitsList = ResultDataParse.graphMapParsing(hitsMap.getData(), null, "yAxis2");
        resultList.addAll(hitsList);

        saveResult(reportId, resultList);
    }

    @Override
    public String getReportKey() {
        return ReportKeys.LoadChart.name();
    }

}
