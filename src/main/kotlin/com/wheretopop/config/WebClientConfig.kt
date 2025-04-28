    package com.wheretopop.config

    import org.springframework.context.annotation.Bean
    import org.springframework.context.annotation.Configuration
    import org.springframework.web.reactive.function.client.WebClient

    @Configuration
    class WebClientConfig {

        @Bean
        fun seoulApiWebClient(): WebClient {
            return WebClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
                .build()
        }

        @Bean
        fun popupApiWebClient(): WebClient {
            return WebClient.builder()
                .baseUrl("https://www.popply.co.kr/popup")
                .build()
        }
    }