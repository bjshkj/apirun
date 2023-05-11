package io.apirun.httpruner.dto;

import lombok.Data;

@Data
public class KafkaDto {

    private String bootstrapServers;

    private String logTopic;
}
