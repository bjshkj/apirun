package io.apirun.commons.consumer;

import com.alibaba.fastjson.JSON;
import io.apirun.commons.utils.LogUtil;
import io.apirun.dto.TaskLogDTO;
import io.apirun.httpruner.service.HttpRunnerResultService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ApiTestTaskLogConsumer {

    public static final String CONSUME_ID = "api_test_task_log_data";
    public static final Integer QUEUE_SIZE = 10000;
    private static final String SEPARATOR = " ";
    private final BlockingQueue<TaskLogDTO> logQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final CopyOnWriteArrayList<TaskLogDTO> logList = new CopyOnWriteArrayList<>();

    @Resource
    private HttpRunnerResultService httpRunnerResultService;
    private boolean isRunning = true;

    @KafkaListener(id = CONSUME_ID, topics = "${kafka.tasklog.topic}", groupId = "httprunner-log2")
    public void consume(ConsumerRecord<?, String> record) throws InterruptedException {
        TaskLogDTO taskLog = JSON.parseObject(record.value(), TaskLogDTO.class);
        LogUtil.info("任务日志："+taskLog);
        logQueue.put(taskLog);
        if (taskLog.getMsg().startsWith("httprunner end")) {
            save();
            log.info("api test finish. taskid:{}", taskLog.getTaskId());
            LogUtil.info("api test finish. taskid:{}", taskLog.getTaskId());
        }
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
                    TaskLogDTO log = logQueue.take();
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
//                    Thread.sleep(20 * 1000);
                } catch (Exception e) {
                    LogUtil.error("handle save error: ", e);
                }
            }
        }).start();
    }


    public synchronized void save() {
        LogUtil.info("save logs size: " + logList.size());
        Map<String, List<TaskLogDTO>> reportLogs = logList.stream().collect(Collectors.groupingBy(TaskLogDTO::getTaskId));
        reportLogs.forEach((taskId, logs) -> {
            StringBuilder content = new StringBuilder();
            for (TaskLogDTO log : logs) {
                content.append(formatTaskLog(log));
                logList.remove(log);
            }
            httpRunnerResultService.savePartContent(taskId, content.toString());
        });
    }

    public static String formatTaskLog(TaskLogDTO taskLogDTO) {

        return String.format("%s %s [%s]  %s\r\n",  taskLogDTO.getTime(), taskLogDTO.getLevel(), taskLogDTO.getLoggerName(),
                taskLogDTO.getMsg());
    }
}
