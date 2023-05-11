package io.metersphere.streaming.engine.consumer;

import io.metersphere.streaming.commons.utils.LogUtil;
import io.metersphere.streaming.model.Log;
import io.metersphere.streaming.service.LogResultService;
import io.metersphere.streaming.service.TestResultService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class LogConsumer {
    public static final String CONSUME_ID = "log-data";
    public static final Integer QUEUE_SIZE = 1000;
    private static final String SEPARATOR = " ";

    @Resource
    private LogResultService logResultService;
    @Resource
    private TestResultService testResultService;

    private final BlockingQueue<Log> logQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final List<Log> logList = new CopyOnWriteArrayList<>();

    private boolean isRunning = true;

    @KafkaListener(id = CONSUME_ID, topics = "${kafka.log.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<?, String> record) throws Exception {
        String value = record.value();
        String reportId = StringUtils.substringBefore(value, SEPARATOR);
        String content = StringUtils.substringAfter(value, SEPARATOR);
        String resourceId = StringUtils.substringBefore(content, SEPARATOR);
        content = StringUtils.substringAfter(content, SEPARATOR);
        int resourceIndex = Integer.parseInt(StringUtils.substringBefore(content, SEPARATOR));
        content = StringUtils.substringAfter(content, SEPARATOR);
        content = StringUtils.appendIfMissing(content, "\n");

        if (StringUtils.contains(content, "Caused by: java.lang.IllegalArgumentException: File")) {
            testResultService.saveErrorMessage(reportId, "测试执行缺少相关文件，例如：csv, jar 等");
        }
        if (StringUtils.contains(content, "Error in NonGUIDriver java.lang.IllegalArgumentException: Problem loading XML from")) {
            testResultService.saveErrorMessage(reportId, "无法解析JMX文件，请确认是否缺少执行需要的 jar 等文件");
        }
        if (StringUtils.contains(content, "Unable to get local host IP address")) {
            testResultService.saveErrorMessage(reportId, "检查资源池节点上的 /etc/hosts 是否有 127.0.0.1 `当前主机名` 这条配置，请确保这个配置存在并删除报告重新执行");
        }
        if (StringUtils.contains(content, "There is insufficient memory for the Java Runtime Environment to continue.")) {
            testResultService.saveErrorMessage(reportId, "资源池没有足够的资源来启动这个测试");
        }

        // 容器退出
        if (StringUtils.contains(content, "Remove container completed")) {
            // todo 容器退出逻辑
        }
        // 手动停止的测试
        if (StringUtils.equals("none", resourceId)) {
            return;
        }

        Log log = Log.builder()
                .reportId(reportId)
                .resourceId(resourceId)
                .resourceIndex(resourceIndex)
                .content(content)
                .build();
        logQueue.put(log);
    }


    @PreDestroy
    public void preDestroy() {
        isRunning = false;
    }

    @PostConstruct
    public void handleQueue() {
        new Thread(() -> {
            while (isRunning) {
                try {
                    Log log = logQueue.take();
                    logList.add(log);
                    // 长度达到 queue_size save 一次
                    int size = logList.size();
                    if (size >= QUEUE_SIZE) {
                        save();
                    }
                } catch (Exception e) {
                    LogUtil.error("handle queue error: ", e);
                }
            }
        }).start();
    }

    @PostConstruct
    public void handleSave() {
        new Thread(() -> {
            while (isRunning) {
                try {
                    // 确保 logs 全部被保存
                    int size = logList.size();
                    if (logQueue.isEmpty() && size > 0 && size < QUEUE_SIZE) {
                        save();
                    }
                    Thread.sleep(20 * 1000);
                } catch (Exception e) {
                    LogUtil.error("handle save error: ", e);
                }
            }
        }).start();
    }


    public synchronized void save() {
        LogUtil.info("save logs size: " + logList.size());
        Map<String, List<Log>> reportLogs = logList.stream().collect(Collectors.groupingBy(this::fetchGroupKey));
        reportLogs.forEach((groupKey, logs) -> {
            String[] ids = StringUtils.split(groupKey, "|");
            String reportId = ids[0];
            String resourceId = ids[1];
            int resourceIndex = Integer.parseInt(ids[2]);
            StringBuilder content = new StringBuilder();
            for (Log log : logs) {
                content.append(log.getContent());
                logList.remove(log);
            }
            Log log = Log.builder()
                    .reportId(reportId)
                    .resourceId(resourceId)
                    .content(content.toString())
                    .resourceIndex(resourceIndex)
                    .build();
            logResultService.savePartContent(log);
        });
    }

    private String fetchGroupKey(Log log) {
        return StringUtils.joinWith("|", log.getReportId(), log.getResourceId(), log.getResourceIndex());
    }
}
