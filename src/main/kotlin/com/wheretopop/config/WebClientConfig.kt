package com.wheretopop.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://api.example.com") // 실제 사용할 API의 baseUrl로 변경해야 합니다
            .build()
    }
} 