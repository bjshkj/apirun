package io.metersphere.streaming.engine.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.streaming.commons.utils.LogUtil;
import io.metersphere.streaming.model.Metric;
import io.metersphere.streaming.service.MetricDataService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import static io.metersphere.streaming.service.MetricDataService.QUEUE_SIZE;

@Service
public class DataConsumer {

    public static final String CONSUME_ID = "metric-data";
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MetricDataService metricDataService;
    private boolean isRunning = true;

    @KafkaListener(id = CONSUME_ID, topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<?, String> record) throws Exception {
        Metric metric = objectMapper.readValue(record.value(), Metric.class);
        if (BooleanUtils.toBoolean(metric.getCompleted())) {
            metricDataService.save();
            metricDataService.completeTest(metric);
            return;
        }
        if (metric.getTimestamp().getTime() == 0) {
            // dubbo sample 有时候会上传一个timestamp为0的结果，忽略
            return;
        }
        metricDataService.addToMetricQueue(metric);
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
                    Metric metric = metricDataService.getMetricQueue().take();
                    metricDataService.addToMetricList(metric);
                    // 长度达到 queue_size save 一次
                    int size = metricDataService.getMetricList().size();
                    if (size >= QUEUE_SIZE) {
                        metricDataService.save();
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
                    // 确保 metrics 全部被保存
                    int size = metricDataService.getMetricList().size();
                    if (metricDataService.getMetricQueue().isEmpty() && size > 0 && size < QUEUE_SIZE) {
                        metricDataService.save();
                    }
                    Thread.sleep(20 * 1000);
                } catch (Exception e) {
                    LogUtil.error("handle save error: ", e);
                }
            }
        }).start();
    }
}
