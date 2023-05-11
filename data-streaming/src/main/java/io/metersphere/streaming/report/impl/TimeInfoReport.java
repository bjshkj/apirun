package io.metersphere.streaming.report.impl;

import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.report.base.ReportTimeInfo;
import org.apache.jmeter.report.processor.FilterConsumer;
import org.apache.jmeter.report.processor.ValueResultData;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.apache.jmeter.report.dashboard.ReportGenerator.BEGIN_DATE_CONSUMER_NAME;
import static org.apache.jmeter.report.dashboard.ReportGenerator.END_DATE_CONSUMER_NAME;

public class TimeInfoReport extends AbstractReport {

    @Override
    public String getReportKey() {
        return ReportKeys.TimeInfo.name();
    }

    @Override
    public void execute() {
        ReportTimeInfo reportTimeInfo = getReportTimeInfo();

        saveResult(reportId, reportTimeInfo);
    }

    private ReportTimeInfo getReportTimeInfo() {

        Map<String, Object> sampleDataMap = sampleContextMap.get(FilterConsumer.class.getSimpleName()).getData();

        ValueResultData beginDateResult = (ValueResultData) sampleDataMap.get(BEGIN_DATE_CONSUMER_NAME);
        ValueResultData endDateResult = (ValueResultData) sampleDataMap.get(END_DATE_CONSUMER_NAME);
        long startTimeStamp = ((Double) beginDateResult.getValue()).longValue();
        long endTimeStamp = ((Double) endDateResult.getValue()).longValue();

        long seconds = Duration.between(Instant.ofEpochMilli(startTimeStamp), Instant.ofEpochMilli(endTimeStamp)).getSeconds();
        ReportTimeInfo reportTimeInfo = new ReportTimeInfo();
        reportTimeInfo.setStartTime(startTimeStamp);
        reportTimeInfo.setEndTime(endTimeStamp);
        reportTimeInfo.setDuration(seconds);
        return reportTimeInfo;
    }

}
