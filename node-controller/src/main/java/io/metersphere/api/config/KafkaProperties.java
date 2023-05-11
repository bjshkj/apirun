package io.metersphere.api.config;

import io.metersphere.node.util.HostAddress;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = KafkaProperties.KAFKA_PREFIX)
@Getter
@Setter
public class KafkaProperties {
    public static final String KAFKA_PREFIX = "kafka";

    private String acks = "0"; // 不要设置all
    private String expectedDelayEndTime = "30000"; // 30s
    private String topic;
    private String consumerGroupId = HostAddress.getLocalIP().replace(".","");
    private String fields;
    private String timestamp;
    private String bootstrapServers;
    private String sampleFilter;
    private String testMode;
    private String parseAllReqHeaders;
    private String parseAllResHeaders;
    private String compressionType;
    private String batchSize;
    private String clientId;
    private String connectionsMaxIdleMs;
    private Ssl ssl = new Ssl();
    private Log log = new Log();

    @Getter
    @Setter
    public static class Ssl {
        private String enabled = "false";
        private String keyPassword;
        private String keystoreLocation;
        private String keystorePassword;
        private String keystoreType;
        private String truststoreLocation;
        private String truststorePassword;
        private String truststoreType;
        private String protocol;
        private String enabledProtocols;
        private String provider;
    }

    @Getter
    @Setter
    public static class Log {
        private String topic;
    }
}
