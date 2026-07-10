package com.example.studentsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.openai")
public class AiConfig {

    private String apiKey;
    private String baseUrl = "https://api.openai.com";
    private String model = "gpt-3.5-turbo";
    private double temperature = 0.7;
    private int maxTokens = 2048;

    @Bean("openaiRestClient")
    public RestClient openaiRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(60000);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .requestFactory(factory)
                .build();
    }
}
