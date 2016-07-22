package com.javie.yaki.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Javie on 16/7/1.
 */
@Configuration
public class PropertiesConfig {
    @Bean
    @ConfigurationProperties(prefix = "rate.limit")
    public RateLimitProperties rateLimitProperties() {
        return new RateLimitProperties();
    }
}
