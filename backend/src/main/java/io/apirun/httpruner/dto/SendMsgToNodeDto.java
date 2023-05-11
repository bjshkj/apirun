package io.apirun.httpruner.dto;

import lombok.Data;

@Data
public class SendMsgToNodeDto {
    private String taskId;

    private String nodeId;

    private String projectId;

    private String image;

    private String gitVersion;

    private String debugMode;

    private String execCasePaths;

    private String dotEnvPath;

    private String hostsPath;

    private KafkaDto kafkaDto;

    private String dataCallbackUrl;
}
