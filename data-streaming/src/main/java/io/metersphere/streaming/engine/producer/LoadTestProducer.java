package io.metersphere.streaming.engine.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.streaming.base.domain.LoadTestReport;
import io.metersphere.streaming.commons.utils.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoadTestProducer {

    @Value("${kafka.test.topic}")
    private String topic;
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Resource
    private ObjectMapper objectMapper;

    public void sendMessage(LoadTestReport loadTestReport) {
        if (loadTestReport == null) {
            LogUtil.error("当前测试已删除");
            return;
        }
        try {
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(loadTestReport));
        } catch (Exception e) {
            LogUtil.error("发送成功通知失败", e);
        }
    }
}
