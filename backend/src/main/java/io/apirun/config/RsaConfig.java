package io.apirun.config;

import io.apirun.commons.utils.RsaKey;
import io.apirun.commons.utils.RsaUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;

@Configuration
public class RsaConfig {
    @Bean
    public RsaKey rsaKey() throws NoSuchAlgorithmException {
        return RsaUtil.createKeys();
    }
}
