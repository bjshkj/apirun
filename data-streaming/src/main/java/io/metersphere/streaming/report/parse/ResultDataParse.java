package io.metersphere.streaming.report.parse;

import io.metersphere.streaming.base.domain.LoadTestReportDetail;
import io.metersphere.streaming.commons.utils.CSVUtils;
import io.metersphere.streaming.commons.utils.CommonBeanFactory;
import io.metersphere.streaming.commons.utils.LogUtil;
import io.metersphere.streaming.commons.utils.MsJMeterUtils;
import io.metersphere.streaming.report.base.ChartsData;
import io.metersphere.streaming.report.graph.consumer.DistributedActiveThreadsGraphConsumer;
import io.metersphere.streaming.report.graph.consumer.ErrorsGraphConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.jmeter.report.core.Sample;
import org.apache.jmeter.report.core.SampleMetadata;
import org.apache.jmeter.report.dashboard.JsonizerVisitor;
import org.apache.jmeter.report.processor.*;
import org.apache.jmeter.report.processor.graph.impl.*;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.item.ExecutionContext;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.jmeter.report.dashboard.ReportGenerator.*;

public class ResultDataParse {

    private static final String DATE_TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";


    public static List<AbstractSampleConsumer> initConsumerList(Integer granularity) {
        List<AbstractSampleConsumer> consumerList = new ArrayList<>();

        // 1
        DistributedActiveThreadsGraphConsumer distributedActiveThreadsGraphConsumer = new DistributedActiveThreadsGraphConsumer();
        distributedActiveThreadsGraphConsumer.setGranularity(granularity);
        distributedActiveThreadsGraphConsumer.initialize();
        consumerList.add(distributedActiveThreadsGraphConsumer);

        ResponseTimeOverTimeGraphConsumer responseTimeOverTimeGraphConsumer = new ResponseTimeOverTimeGraphConsumer();
        responseTimeOverTimeGraphConsumer.setGranularity(granularity);
        responseTimeOverTimeGraphConsumer.initialize();
        consumerList.add(responseTimeOverTimeGraphConsumer);

        ResponseTimePercentilesOverTimeGraphConsumer responseTimePercentilesOverTimeGraphConsumer = new ResponseTimePercentilesOverTimeGraphConsumer();
        responseTimePercentilesOverTimeGraphConsumer.setGranularity(granularity);
        responseTimePercentilesOverTimeGraphConsumer.initialize();
        consumerList.add(responseTimePercentilesOverTimeGraphConsumer);

        BytesThroughputGraphConsumer bytesThroughputGraphConsumer = new BytesThroughputGraphConsumer();
        bytesThroughputGraphConsumer.setGranularity(granularity);
        bytesThroughputGraphConsumer.initialize();
        consumerList.add(bytesThroughputGraphConsumer);

        LatencyOverTimeGraphConsumer latencyOverTimeGraphConsumer = new LatencyOverTimeGraphConsumer();
        latencyOverTimeGraphConsumer.setGranularity(granularity);
        latencyOverTimeGraphConsumer.initialize();
        consumerList.add(latencyOverTimeGraphConsumer);

        ConnectTimeOverTimeGraphConsumer connectTimeOverTimeGraphConsumer = new ConnectTimeOverTimeGraphConsumer();
        connectTimeOverTimeGraphConsumer.setGranularity(granularity);
        connectTimeOverTimeGraphConsumer.initialize();
        consumerList.add(connectTimeOverTimeGraphConsumer);

        // 2
        HitsPerSecondGraphConsumer hitsPerSecondGraphConsumer = new HitsPerSecondGraphConsumer();
        hitsPerSecondGraphConsumer.setGranularity(granularity);
        hitsPerSecondGraphConsumer.initialize();
        consumerList.add(hitsPerSecondGraphConsumer);

        CodesPerSecondGraphConsumer responseCodeOverTimeGraphConsumer = new CodesPerSecondGraphConsumer();
        responseCodeOverTimeGraphConsumer.setGranularity(granularity);
        responseCodeOverTimeGraphConsumer.initialize();
        consumerList.add(responseCodeOverTimeGraphConsumer);

        TransactionsPerSecondGraphConsumer transactionsPerSecondGraphConsumer = new TransactionsPerSecondGraphConsumer();
        transactionsPerSecondGraphConsumer.setGranularity(granularity);
        transactionsPerSecondGraphConsumer.initialize();
        consumerList.add(transactionsPerSecondGraphConsumer);

        TotalTPSGraphConsumer totalTPSGraphConsumer = new TotalTPSGraphConsumer();
        totalTPSGraphConsumer.setGranularity(granularity);
        totalTPSGraphConsumer.initialize();
        consumerList.add(totalTPSGraphConsumer);

        // 暂不支持
//        ResponseTimeVSRequestGraphConsumer responseTimeVSRequestGraphConsumer = new ResponseTimeVSRequestGraphConsumer();
//        responseTimeVSRequestGraphConsumer.setGranularity(granularity);
//        responseTimeVSRequestGraphConsumer.initialize();
//        consumerList.add(responseTimeVSRequestGraphConsumer);
//
//        LatencyVSRequestGraphConsumer latencyVSRequestGraphConsumer = new LatencyVSRequestGraphConsumer();
//        latencyVSRequestGraphConsumer.setGranularity(granularity);
//        latencyVSRequestGraphConsumer.initialize();
//        consumerList.add(latencyVSRequestGraphConsumer);

        // 3
        ResponseTimePercentilesGraphConsumer timePercentilesGraphConsumer = new ResponseTimePercentilesGraphConsumer();
        timePercentilesGraphConsumer.initialize();
        consumerList.add(timePercentilesGraphConsumer);

        TimeVSThreadGraphConsumer timeVSThreadGraphConsumer = new TimeVSThreadGraphConsumer();
        timeVSThreadGraphConsumer.initialize();
        consumerList.add(timeVSThreadGraphConsumer);

        ResponseTimeDistributionGraphConsumer responseTimeDistributionGraphConsumer = new ResponseTimeDistributionGraphConsumer();
        responseTimeDistributionGraphConsumer.setGranularity(granularity);
        responseTimeDistributionGraphConsumer.initialize();
        consumerList.add(responseTimeDistributionGraphConsumer);


        ErrorsGraphConsumer errorsGraphConsumer = new ErrorsGraphConsumer();
        errorsGraphConsumer.setGranularity(granularity);
        errorsGraphConsumer.initialize();
        consumerList.add(errorsGraphConsumer);

        StatisticsSummaryConsumer statisticsSummaryConsumer = new StatisticsSummaryConsumer();
        statisticsSummaryConsumer.setHasOverallResult(true);
        consumerList.add(statisticsSummaryConsumer);
        consumerList.add(new RequestsSummaryConsumer());
        consumerList.add(new ErrorsSummaryConsumer());
        consumerList.add(new Top5ErrorsBySamplerConsumer());

        FilterConsumer dateRangeConsumer = createFilterByDateRange();
        dateRangeConsumer.addSampleConsumer(createBeginDateConsumer());
        dateRangeConsumer.addSampleConsumer(createEndDateConsumer());

        consumerList.add(dateRangeConsumer);
        return consumerList;
    }

    public static <T> List<T> summaryMapParsing(Map<String, Object> map, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (String key : map.keySet()) {
            MapResultData mapResultData = (MapResultData) map.get(key);
            ListResultData items = (ListResultData) mapResultData.getResult("items");
            if (items.getSize() > 0) {
                for (int i = 0; i < items.getSize(); i++) {
                    MapResultData resultData = (MapResultData) items.get(i);
                    ListResultData data = (ListResultData) resultData.getResult("data");
                    list.addAll(getData(clazz, data));
                }
            }
            MapResultData overall = (MapResultData) mapResultData.getResult("overall");
            if (overall != null) {
                ListResultData overallData = (ListResultData) overall.getResult("data");
                list.addAll(getData(clazz, overallData));
            }
        }
        return list;
    }

    private static <T> List<T> getData(Class<T> clazz, ListResultData data) {
        List<T> list = new ArrayList<>();
        int size = data.getSize();
        String[] strArray = new String[size];
        if (size > 0) {
            T t = null;
            for (int j = 0; j < size; j++) {
                ValueResultData valueResultData = (ValueResultData) data.get(j);
                Object value = valueResultData.getValue();
                if (value == null) {
                    strArray[j] = "";
                } else {
                    String input = value.toString();
                    // unicode 转义,
                    strArray[j] = StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(input));
                }
            }

            try {
                t = setParam(clazz, strArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(t);
        }
        return list;
    }

    public static List<ChartsData> graphMapParsing(Map<String, Object> map, String seriesName, String yAxis) {
        List<ChartsData> list = new ArrayList<>();
        // ThreadGroup
        for (String key : map.keySet()) {
            MapResultData mapResultData = (MapResultData) map.get(key);
            ResultData maxY = mapResultData.getResult("maxY");
            ListResultData series = (ListResultData) mapResultData.getResult("series");
            if (series.getSize() > 0) {
                for (int j = 0; j < series.getSize(); j++) {
                    MapResultData resultData = (MapResultData) series.get(j);
                    // data, isOverall, label, isController
                    ListResultData data = (ListResultData) resultData.getResult("data");
                    ValueResultData label = (ValueResultData) resultData.getResult("label");

                    if (data.getSize() > 0) {
                        for (int i = 0; i < data.getSize(); i++) {
                            ListResultData listResultData = (ListResultData) data.get(i);
                            String result = listResultData.accept(new JsonizerVisitor());
                            result = result.substring(1, result.length() - 1);
                            String[] split = result.split(",");
                            ChartsData chartsData = new ChartsData();
                            BigDecimal bigDecimal = new BigDecimal(split[0]);
                            String timeStamp = bigDecimal.toPlainString();
                            // timestamp 可能会出现0.0
                            if (timeStamp.startsWith("0")) {
                                timeStamp = "0";
                            }
                            String time = null;
                            try {
                                time = formatDate(stampToDate(DATE_TIME_PATTERN, timeStamp));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            chartsData.setxAxis(time);
                            if (StringUtils.equals("yAxis2", yAxis)) {
                                chartsData.setyAxis2(new BigDecimal(split[1].trim()));
                                chartsData.setyAxis(new BigDecimal(-1));
                            } else {
                                chartsData.setyAxis(new BigDecimal(split[1].trim()));
                                chartsData.setyAxis2(new BigDecimal(-1));
                            }
                            if (series.getSize() == 1) {
                                if (StringUtils.isBlank(seriesName)) {
                                    chartsData.setGroupName((String) label.getValue());
                                } else {
                                    chartsData.setGroupName(seriesName);
                                }
                            } else {
                                chartsData.setGroupName((String) label.getValue());
                            }
                            list.add(chartsData);
                        }
                    }
                }

            }
        }
        return list;
    }

    public static Map<String, SampleContext> initJMeterConsumer(String reportId, List<AbstractSampleConsumer> consumerList) {
        int row = 0;
        // 使用反射获取properties
        MsJMeterUtils.loadJMeterProperties("jmeter.properties");
        SampleMetadata sampleMetaData = createTestMetaData();

        Map<String, SampleContext> resultSampleContext = new HashMap<>();
        consumerList.forEach(consumer -> {
            SampleContext sampleContext = new SampleContext();
            consumer.setSampleContext(sampleContext);
            consumer.startConsuming();
            resultSampleContext.put(consumer.getClass().getSimpleName(), sampleContext);
        });

        SqlSessionFactory sqlSessionFactory = CommonBeanFactory.getBean(SqlSessionFactory.class);
        MyBatisCursorItemReader<LoadTestReportDetail> myBatisCursorItemReader = new MyBatisCursorItemReaderBuilder<LoadTestReportDetail>()
                .sqlSessionFactory(sqlSessionFactory)
                // 设置queryId
                .queryId("io.metersphere.streaming.base.mapper.ext.ExtLoadTestReportMapper.fetchTestReportDetails")
                .build();
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("reportId", reportId);
            myBatisCursorItemReader.setParameterValues(param);
            myBatisCursorItemReader.open(new ExecutionContext());
            LoadTestReportDetail loadTestReportDetail;
            while ((loadTestReportDetail = myBatisCursorItemReader.read()) != null) {
                //
                String content = loadTestReportDetail.getContent();
                StringTokenizer tokenizer = new StringTokenizer(content, "\n");
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    String[] data = CSVUtils.parseLine(line);
                    Sample sample = new Sample(row++, sampleMetaData, data);

                    consumerList.forEach(consumer -> {
                        try {
                            consumer.consume(sample, 0);
                        } catch (Exception e) {
                            LogUtil.error(consumer.getName(), e);
                        }
                    });
                }
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            myBatisCursorItemReader.close();
        }

        consumerList.forEach(SampleConsumer::stopConsuming);

        return resultSampleContext;
    }

    // Create a static SampleMetadataObject
    private static SampleMetadata createTestMetaData() {
        String columnsString = "timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,sentBytes,grpThreads,allThreads,URL,Latency,IdleTime,Connect";

        String[] columns = new String[17];
        int lastComa = 0;
        int columnIndex = 0;
        for (int i = 0; i < columnsString.length(); i++) {
            if (columnsString.charAt(i) == ',') {
                columns[columnIndex] = columnsString.substring(lastComa, i);
                lastComa = i + 1;
                columnIndex++;
            } else if (i + 1 == columnsString.length()) {
                columns[columnIndex] = columnsString.substring(lastComa, i + 1);
            }
        }
        return new SampleMetadata(',', columns);
    }

    private static String stampToDate(String pattern, String timeStamp) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timeStamp)), ZoneId.systemDefault());
        return localDateTime.format(dateTimeFormatter);
    }

    private static String formatDate(String dateString) throws ParseException {
        SimpleDateFormat before = new SimpleDateFormat(DATE_TIME_PATTERN);
        SimpleDateFormat after = new SimpleDateFormat(TIME_PATTERN);
        return after.format(before.parse(dateString));
    }

    private static <T> T setParam(Class<T> clazz, Object[] args)
            throws Exception {
        if (clazz == null || args == null) {
            throw new IllegalArgumentException();
        }
        T t = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > args.length) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            fields[i].set(t, args[i]);
        }
        return t;
    }


    private static FilterConsumer createFilterByDateRange() {
        FilterConsumer dateRangeFilter = new FilterConsumer();
        dateRangeFilter.setName(DATE_RANGE_FILTER_CONSUMER_NAME);
        dateRangeFilter.setSamplePredicate(sample -> true);
        return dateRangeFilter;
    }

    private static AggregateConsumer createEndDateConsumer() {
        AggregateConsumer endDateConsumer = new AggregateConsumer(
                new MaxAggregator(), sample -> (double) sample.getEndTime());
        endDateConsumer.setName(END_DATE_CONSUMER_NAME);
        return endDateConsumer;
    }

    private static AggregateConsumer createBeginDateConsumer() {
        AggregateConsumer beginDateConsumer = new AggregateConsumer(
                new MinAggregator(), sample -> (double) sample.getStartTime());
        beginDateConsumer.setName(BEGIN_DATE_CONSUMER_NAME);
        return beginDateConsumer;
    }
}
