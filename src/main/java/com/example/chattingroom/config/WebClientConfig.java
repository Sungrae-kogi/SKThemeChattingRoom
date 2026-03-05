package com.example.chattingroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Gemini api 와 통신할 Config
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder){
        // 실무에서는 여기에 타임아웃(3초 이상 안 오면 연결을 끊기)나 기본 헤더 설정 등을 추가하여 고도화가 가능.

        return builder.build();
    }
}
