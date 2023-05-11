package io.metersphere.node.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class KafkaProducer {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, String report) {
        this.kafkaTemplate.send(topic, report);
    }
}
