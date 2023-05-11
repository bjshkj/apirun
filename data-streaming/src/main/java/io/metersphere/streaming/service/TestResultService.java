package io.metersphere.streaming.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.streaming.base.domain.*;
import io.metersphere.streaming.base.mapper.LoadTestMapper;
import io.metersphere.streaming.base.mapper.LoadTestReportDetailMapper;
import io.metersphere.streaming.base.mapper.LoadTestReportMapper;
import io.metersphere.streaming.base.mapper.LoadTestReportResultMapper;
import io.metersphere.streaming.base.mapper.ext.ExtLoadTestMapper;
import io.metersphere.streaming.base.mapper.ext.ExtLoadTestReportMapper;
import io.metersphere.streaming.base.mapper.ext.ExtLoadTestReportResultMapper;
import io.metersphere.streaming.commons.constants.GranularityData;
import io.metersphere.streaming.commons.constants.ReportKeys;
import io.metersphere.streaming.commons.constants.TestStatus;
import io.metersphere.streaming.commons.utils.CommonBeanFactory;
import io.metersphere.streaming.commons.utils.CompressUtils;
import io.metersphere.streaming.commons.utils.LogUtil;
import io.metersphere.streaming.config.JmeterReportProperties;
import io.metersphere.streaming.engine.producer.LoadTestProducer;
import io.metersphere.streaming.model.AdvancedConfig;
import io.metersphere.streaming.model.Metric;
import io.metersphere.streaming.model.PressureConfig;
import io.metersphere.streaming.report.ReportGeneratorFactory;
import io.metersphere.streaming.report.base.ReportTimeInfo;
import io.metersphere.streaming.report.base.TestOverview;
import io.metersphere.streaming.report.impl.AbstractReport;
import io.metersphere.streaming.report.parse.ResultDataParse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.jmeter.report.processor.SampleContext;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TestResultService {
    public static final String HEADERS = "timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,sentBytes,grpThreads,allThreads,URL,Latency,IdleTime,Connect";

    @Resource
    private LoadTestReportMapper loadTestReportMapper;
    @Resource
    private ExtLoadTestReportMapper extLoadTestReportMapper;
    @Resource
    private LoadTestMapper loadTestMapper;
    @Resource
    private LoadTestReportDetailMapper loadTestReportDetailMapper;
    @Resource
    private TestResultSaveService testResultSaveService;
    @Resource
    private ExtLoadTestMapper extLoadTestMapper;
    @Resource
    private FileService fileService;
    @Resource
    private LoadTestProducer loadTestProducer;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private LoadTestReportResultMapper loadTestReportResultMapper;
    @Resource
    private ExtLoadTestReportResultMapper extLoadTestReportResultMapper;

    public static final String TEMP_DIRECTORY_PATH = FileUtils.getTempDirectoryPath();

    static {
        LogUtil.info("Temp dir: " + TEMP_DIRECTORY_PATH);
    }

    ScheduledExecutorService completeThreadPool = Executors.newScheduledThreadPool(10);
    ExecutorService reportThreadPool = Executors.newFixedThreadPool(30);

    @Transactional(rollbackFor = Exception.class)
    public void savePartContent(String reportId, String testId, String content) {
        // 更新状态
        extLoadTestReportMapper.updateStatus(reportId, TestStatus.Running.name(), TestStatus.Starting.name());
        extLoadTestMapper.updateStatus(testId, TestStatus.Running.name(), TestStatus.Starting.name());

        LoadTestReportDetail record = new LoadTestReportDetail();
        record.setReportId(reportId);
        record.setContent(content);
        extLoadTestReportMapper.insert(record);

        // 计算结果
        preGenerateReport(reportId);
    }

    public String convertToLine(Metric metric) {
        //timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,sentBytes,grpThreads,allThreads,URL,Latency,IdleTime,Connect
        long start = metric.getTimestamp().getTime();
        StringBuilder content = new StringBuilder();
        content.append(start).append(",");
        content.append(metric.getResponseTime()).append(",");
        content.append(warp(metric.getSampleLabel())).append(",");
        content.append(metric.getResponseCode()).append(",");
        // response message
        content.append(warp(metric.getResponseMessage())).append(",");
        content.append(warp(metric.getThreadName())).append(",");
        content.append(metric.getDataType()).append(",");
        content.append(metric.getSuccess()).append(",");
        // failure message contains \n , etc.
        content.append(warp(convertFailureMessage(metric))).append(",");
        content.append(metric.getBytes()).append(",");
        content.append(metric.getSentBytes()).append(",");
        content.append(metric.getGrpThreads()).append(",");
        content.append(metric.getAllThreads()).append(",");
        // 处理url换行问题
        if (StringUtils.isNotBlank(metric.getUrl())) {
            content.append(warp(StringUtils.deleteWhitespace(metric.getUrl()))).append(",");
        } else {
            content.append(",");
        }
        content.append(metric.getLatency()).append(",");
        content.append(metric.getIdleTime()).append(",");
        content.append(metric.getConnectTime()).append("\n");
        return content.toString();
    }

    private String warp(String value) {
        // 1 先处理是否包含双引号
        if (StringUtils.contains(value, "\"")) {
            value = StringUtils.wrapIfMissing(StringUtils.replace(value, "\"", "\"\""), "\"");
        }
        // 2 然后处理是否包含逗号、 \n
        if (StringUtils.contains(value, ",") || StringUtils.contains(value, "\n")) {
            value = StringUtils.wrapIfMissing(value, "\"");
        }
        // 返回结果
        return value;
    }

    private String convertFailureMessage(Metric metric) {
        return StringUtils.remove(metric.getFailureMessage(), "\n");
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeReport(Metric metric) {
        LoadTestReport report = loadTestReportMapper.selectByPrimaryKey(metric.getReportId());
        if (report == null) {
            LogUtil.info("Report is null.");
            return;
        }
        //更新complete count
        extLoadTestReportResultMapper.updateReportCompleteCount(metric.getReportId());

        int count = extLoadTestReportResultMapper.selectReportCompleteCount(metric.getReportId());

        // count == 0 表示最后一个结束消息已经上传
        if (count > 0) {
            LogUtil.info("等待其他节点结束: " + report.getTestId());
            return;
        }
        LoadTestWithBLOBs loadTest = new LoadTestWithBLOBs();
        loadTest.setId(report.getTestId());
        loadTest.setStatus(TestStatus.Completed.name());
        loadTestMapper.updateByPrimaryKeySelective(loadTest);

        LogUtil.info("测试[{}]结束，reportId:{}", report.getTestId(), report.getId());
        // 确保计算报告完全执行, 等待其他节点都保存后执行计算
        completeThreadPool.submit(() -> generateReportComplete(report.getId()));
    }

    private void saveJtlFile(String reportId) {
        LoadTestReportDetailExample example1 = new LoadTestReportDetailExample();
        example1.createCriteria().andReportIdEqualTo(reportId);
        if (loadTestReportDetailMapper.countByExample(example1) < 2) {
            return;
        }
        SqlSessionFactory sqlSessionFactory = CommonBeanFactory.getBean(SqlSessionFactory.class);
        MyBatisCursorItemReader<LoadTestReportDetail> myBatisCursorItemReader = new MyBatisCursorItemReaderBuilder<LoadTestReportDetail>()
                .sqlSessionFactory(sqlSessionFactory)
                // 设置queryId
                .queryId("io.metersphere.streaming.base.mapper.ext.ExtLoadTestReportMapper.fetchTestReportDetails")
                .build();
        String filename = reportId + "_" + RandomStringUtils.randomAlphabetic(5) + ".jtl";
        try (
                FileWriter fw = new FileWriter(TEMP_DIRECTORY_PATH + File.separator + filename, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)
        ) {
            // 写入表头
            out.println(HEADERS);
            Map<String, Object> param = new HashMap<>();
            param.put("reportId", reportId);
            myBatisCursorItemReader.setParameterValues(param);
            myBatisCursorItemReader.open(new ExecutionContext());
            LoadTestReportDetail loadTestReportDetail;
            while ((loadTestReportDetail = myBatisCursorItemReader.read()) != null) {
                String content = loadTestReportDetail.getContent();
                out.print(content);
            }
        } catch (Exception e) {
            LogUtil.error("写入文件失败: ", e);
        } finally {
            myBatisCursorItemReader.close();
        }

        try {
            File file = new File(TEMP_DIRECTORY_PATH + File.separator + filename);
            File zipFile = new File(TEMP_DIRECTORY_PATH + File.separator + filename + ".zip");
            CompressUtils.zipFiles(file, zipFile); // 先进行压缩文件
            FileMetadata fileMetadata = fileService.saveFile(zipFile, reportId);
            LoadTestReportWithBLOBs loadTestReportWithBLOBs = new LoadTestReportWithBLOBs();
            loadTestReportWithBLOBs.setFileId(fileMetadata.getId());
            loadTestReportWithBLOBs.setId(reportId);
            loadTestReportMapper.updateByPrimaryKeySelective(loadTestReportWithBLOBs);
            FileUtils.forceDelete(file);
            FileUtils.forceDelete(zipFile);
        } catch (Exception e) {
            LogUtil.error("保存文件文件失败: ", e);
        }
    }

    public void preGenerateReport(String reportId) {
        // 检查 report_status
        boolean set = testResultSaveService.isReportingSet(reportId);
        if (!set) {
            LogUtil.info("report generator is running.");
            return;
        }
        completeThreadPool.submit(() -> generateReport(reportId));
    }

    private void generateReportComplete(String reportId) {
        LoadTestReportWithBLOBs report = new LoadTestReportWithBLOBs();
        report.setId(reportId);
        report.setUpdateTime(System.currentTimeMillis());

        // 测试结束后执行计算报告
        report.setStatus(TestStatus.Reporting.name());
        loadTestReportMapper.updateByPrimaryKeySelective(report);
        //检查状态
        while (!testResultSaveService.isReportingSet(reportId)) {
            try {
                Thread.sleep(20_000);
            } catch (InterruptedException e) {
                LogUtil.error(e);
            }
        }
        // 强制执行一次生成报告
        generateReport(reportId);
        // 保存jtl
        saveJtlFile(reportId);
        // 标记结束
        testResultSaveService.saveReportCompletedStatus(reportId);

        // 测试结束后保存状态
        report.setUpdateTime(System.currentTimeMillis());
        report.setStatus(TestStatus.Completed.name());
        loadTestReportMapper.updateByPrimaryKeySelective(report);
        // 发送成功通知
        LoadTestReportWithBLOBs loadTestReport = loadTestReportMapper.selectByPrimaryKey(reportId);
        loadTestProducer.sendMessage(loadTestReport);
        // 清理中间文件
        LoadTestReportDetailExample example2 = new LoadTestReportDetailExample();
        example2.createCriteria().andReportIdEqualTo(report.getId());
        loadTestReportDetailMapper.deleteByExample(example2);
    }

    public void generateReport(String reportId) {
        long start = System.currentTimeMillis();
        LoadTestReportDetailExample example = new LoadTestReportDetailExample();
        example.createCriteria().andReportIdEqualTo(reportId);
        // 防止中间文件删除之后又执行生成报告导致报错的问题
        if (loadTestReportDetailMapper.countByExample(example) < 2) {
            return;
        }
        // 获取聚合时间
        Integer granularity = getGranularity(reportId);
        List<AbstractReport> reportGenerators = ReportGeneratorFactory.getReportGenerators();
        LogUtil.info("report generators size: {}", reportGenerators.size());
        CountDownLatch countDownLatch = new CountDownLatch(reportGenerators.size());

        Map<String, SampleContext> sampleContextMap = ResultDataParse.initJMeterConsumer(reportId, ResultDataParse.initConsumerList(granularity));

        reportGenerators.forEach(r -> reportThreadPool.execute(() -> {
            LogUtil.info("Report Key: " + r.getReportKey());
            r.init(reportId, sampleContextMap);
            try {
                r.execute();
            } catch (Exception e) {
                LogUtil.error(e);
            } finally {
                countDownLatch.countDown();
            }
        }));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LogUtil.error(e);
        } finally {
            saveReportOverview(reportId);
            saveReportTimeInfo(reportId);
            testResultSaveService.saveReportReadyStatus(reportId);
        }
        LogUtil.info("本次报告[{}]计算结束耗时: {}ms", reportId, System.currentTimeMillis() - start);
    }

    private void saveReportOverview(String reportId) {
        LoadTestReportResultExample example1 = new LoadTestReportResultExample();
        example1.createCriteria().andReportIdEqualTo(reportId).andReportKeyEqualTo(ReportKeys.Overview.name());
        List<LoadTestReportResult> loadTestReportResults = loadTestReportResultMapper.selectByExampleWithBLOBs(example1);
        if (loadTestReportResults.size() > 0) {
            LoadTestReportResult loadTestReportResult = loadTestReportResults.get(0);
            String reportValue = loadTestReportResult.getReportValue();
            try {
                TestOverview testOverview = objectMapper.readValue(reportValue, TestOverview.class);
                LoadTestReportWithBLOBs report = new LoadTestReportWithBLOBs();
                report.setId(reportId);
                report.setMaxUsers(testOverview.getMaxUsers());
                report.setAvgResponseTime(testOverview.getAvgResponseTime());
                report.setTps(testOverview.getAvgTransactions());
                loadTestReportMapper.updateByPrimaryKeySelective(report);
            } catch (JsonProcessingException e) {
                LogUtil.error(e);
            }
        }
    }

    private void saveReportTimeInfo(String reportId) {
        LoadTestReportResultExample example1 = new LoadTestReportResultExample();
        example1.createCriteria().andReportIdEqualTo(reportId).andReportKeyEqualTo(ReportKeys.TimeInfo.name());
        List<LoadTestReportResult> loadTestReportResults = loadTestReportResultMapper.selectByExampleWithBLOBs(example1);
        if (loadTestReportResults.size() > 0) {
            LoadTestReportResult loadTestReportResult = loadTestReportResults.get(0);
            String reportValue = loadTestReportResult.getReportValue();
            try {
                ReportTimeInfo timeInfo = objectMapper.readValue(reportValue, ReportTimeInfo.class);
                LoadTestReportWithBLOBs report = new LoadTestReportWithBLOBs();
                report.setId(reportId);
                report.setTestStartTime(timeInfo.getStartTime());
                report.setTestEndTime(timeInfo.getEndTime());
                report.setTestDuration(timeInfo.getDuration());
                loadTestReportMapper.updateByPrimaryKeySelective(report);
            } catch (JsonProcessingException e) {
                LogUtil.error(e);
            }
        }
    }

    public Integer getGranularity(String reportId) {
        Integer granularity = CommonBeanFactory.getBean(JmeterReportProperties.class).getGranularity();
        try {
            LoadTestReportWithBLOBs report = loadTestReportMapper.selectByPrimaryKey(reportId);
            LoadTestWithBLOBs loadTest = loadTestMapper.selectByPrimaryKey(report.getTestId());
            AdvancedConfig advancedConfig = objectMapper.readValue(loadTest.getAdvancedConfiguration(), AdvancedConfig.class);
            if (advancedConfig.getGranularity() != null) {
                granularity = advancedConfig.getGranularity();
                return granularity * 1000; // 单位是ms
            }
            AtomicReference<Integer> maxDuration = new AtomicReference<>(0);
            List<List<PressureConfig>> pressureConfigLists = objectMapper.readValue(loadTest.getLoadConfiguration(), new TypeReference<List<List<PressureConfig>>>() {
            });
            // 按照最长的执行时间来确定
            pressureConfigLists.forEach(pcList -> {

                Optional<Integer> maxOp = pcList.stream()
                        .filter(pressureConfig -> StringUtils.equalsAnyIgnoreCase(pressureConfig.getKey(), "hold", "duration"))
                        .map(pressureConfig -> (Integer) pressureConfig.getValue())
                        .max(Comparator.naturalOrder());
                Integer max = maxOp.orElse(0);
                if (maxDuration.get() < max) {
                    maxDuration.set(max);
                }
            });
            Optional<GranularityData.Data> dataOptional = GranularityData.dataList.stream()
                    .filter(data -> maxDuration.get() >= data.getStart() && maxDuration.get() <= data.getEnd())
                    .findFirst();

            if (dataOptional.isPresent()) {
                GranularityData.Data data = dataOptional.get();
                granularity = data.getGranularity();
            }

        } catch (JsonProcessingException e) {
            LogUtil.error(e);
        }
        return granularity;
    }


    public void saveErrorMessage(String reportId, String message) {
        LoadTestReportWithBLOBs loadTestReport = new LoadTestReportWithBLOBs();
        loadTestReport.setId(reportId);
        loadTestReport.setStatus(TestStatus.Error.name());
        loadTestReport.setUpdateTime(System.currentTimeMillis());
        loadTestReport.setDescription(message);
        loadTestReportMapper.updateByPrimaryKeySelective(loadTestReport);
        // 查询 test_id
        LoadTestReport testReportFromDB = loadTestReportMapper.selectByPrimaryKey(reportId);
        LoadTestWithBLOBs loadTest = new LoadTestWithBLOBs();
        loadTest.setId(testReportFromDB.getTestId());
        loadTest.setStatus(TestStatus.Error.name());
        loadTest.setDescription(message);
        loadTest.setUpdateTime(System.currentTimeMillis());
        loadTestMapper.updateByPrimaryKeySelective(loadTest);
    }
}
