package io.metersphere.streaming.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic}")
    private String topic;
    @Value("${kafka.log.topic}")
    private String logTopic;
    @Value("${kafka.test.topic}")
    private String testTopic;
    @Value("${kafka.partitions:3}")
    private Integer partitions;
    @Value("${kafka.replicas:1}")
    private Integer replicas;

    @Bean
    public NewTopic dataTopic() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic logTopic() {
        return TopicBuilder.name(logTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic loadTestTopic() {
        return TopicBuilder.name(testTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
