package com.bleaf.telegram.server.torrent.domain.tfreeca.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "telegram.btor.tfreeca")
public class TfreecaConfig {
    String searchUrl;
    String baseUrl;
    String downloadUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
