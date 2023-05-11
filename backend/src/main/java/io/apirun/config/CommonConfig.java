package io.apirun.config;

import io.apirun.common.Qcm.config.IQConf;
import io.apirun.common.Qcm.pro.CommonQCMConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public IQConf getIQConf(){
        return new CommonQCMConfig();
    }
}
