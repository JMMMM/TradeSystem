package com.wujm1.tradesystem.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author wujiaming
 * @date 2024-09-02 17:07
 */
@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate crawlerRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
