package io.metersphere.streaming;

import io.metersphere.streaming.config.JmeterReportProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {QuartzAutoConfiguration.class})
@EnableConfigurationProperties({
        JmeterReportProperties.class
})
//@PropertySource(value = {"file:/opt/metersphere/conf/metersphere.properties"}, encoding = "UTF-8", ignoreResourceNotFound = true)
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
