package io.metersphere.node.dto;

import lombok.Data;

/**
 * kafka
 */
@Data
public class KafkaDto {
    private String bootstrapServers;  //kafka访问ip
    private String logTopic;         //日志回传topic
}
