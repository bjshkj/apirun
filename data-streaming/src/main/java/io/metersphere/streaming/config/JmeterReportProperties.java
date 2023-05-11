package io.metersphere.streaming.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = JmeterReportProperties.JMETER_PREFIX)
@Setter
@Getter
public class JmeterReportProperties {
    public static final String JMETER_PREFIX = "jmeter.report";

    private Integer granularity = 60000;
}
