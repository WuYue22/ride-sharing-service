package com.ridesharing.passengermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
@Profile("passenger")
@Configuration
public class RestTemplateConfig {
    @Primary
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
