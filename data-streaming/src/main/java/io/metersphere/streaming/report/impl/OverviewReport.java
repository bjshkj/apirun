package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ChartsData;
import io.metersphere.streaming.report.base.Statistics;
import io.metersphere.streaming.report.base.TestOverview;
import io.metersphere.streaming.report.graph.consumer.DistributedActiveThreadsGraphConsumer;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.report.processor.*;
import org.apache.jmeter.report.processor.graph.impl.HitsPerSecondGraphConsumer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OverviewReport extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.Overview.name();
    }

    private final DecimalFormat format4 = new DecimalFormat("0.0000");

    @Override
    public void execute() {
        TestOverview testOverview = new TestOverview();

        SampleContext activeDataMap = sampleContextMap.get(DistributedActiveThreadsGraphConsumer.class.getSimpleName());
        List<ChartsData> usersList = ResultDataParse.graphMapParsing(activeDataMap.getData(), "", "yAxis");
        Map<String, List<ChartsData>> collect = usersList.stream().collect(Collectors.groupingBy(ChartsData::getxAxis));
        AtomicInteger maxUser = new AtomicInteger();
        collect.forEach((k, cs) -> {
            double sum = cs.stream().map(ChartsData::getyAxis).mapToDouble(BigDecimal::doubleValue).sum();
            sum = Math.ceil(sum);
            if (maxUser.get() < sum) {
                maxUser.set((int) sum);
            }
        });

        SampleContext hitsDataMap = sampleContextMap.get(HitsPerSecondGraphConsumer.class.getSimpleName());
        List<ChartsData> hitsList = ResultDataParse.graphMapParsing(hitsDataMap.getData(), "", "yAxis2");
        double hits = hitsList.stream().map(ChartsData::getyAxis2)
                .mapToDouble(BigDecimal::doubleValue)
                .average().orElse(0);

        SampleContext errorDataMap = sampleContextMap.get(StatisticsSummaryConsumer.class.getSimpleName());
        List<Statistics> statisticsList = ResultDataParse.summaryMapParsing(errorDataMap.getData(), Statistics.class);

        if (CollectionUtils.isNotEmpty(statisticsList)) {
            int size = statisticsList.size();
            Statistics statistics = statisticsList.get(size - 1);
            if ("[res_key=reportgenerator_summary_total]".equals(statistics.getLabel())) {
                String transactions = statistics.getTransactions();
                String tp90 = statistics.getTp90();
                String responseTime = statistics.getAverage();
                String avgBandwidth = statistics.getReceived();
                //
                testOverview.setAvgTransactions(format2.format(Double.parseDouble(transactions)));
                testOverview.setAvgResponseTime(format4.format(Double.parseDouble(responseTime) / 1000));
                testOverview.setResponseTime90(format4.format(Double.parseDouble(tp90) / 1000));
                testOverview.setAvgBandwidth(format2.format(Double.parseDouble(avgBandwidth)));
            }
        }


        SampleContext sampleDataMap = sampleContextMap.get(RequestsSummaryConsumer.class.getSimpleName());
        Map<String, Object> data = sampleDataMap.getData();
        String error = "";
        for (String key : data.keySet()) {
            MapResultData mapResultData = (MapResultData) data.get(key);
            ResultData koPercent = mapResultData.getResult("KoPercent");
            error = ((ValueResultData) koPercent).getValue().toString();
        }

        testOverview.setMaxUsers(String.valueOf(maxUser.get()));
        testOverview.setAvgThroughput(format2.format(hits));

        testOverview.setErrors(format4.format(Double.valueOf(error)));

        saveResult(reportId, testOverview);
    }
}
