    package com.wheretopop.config

    import org.springframework.context.annotation.Bean
    import org.springframework.context.annotation.Configuration
    import org.springframework.web.reactive.function.client.WebClient
    import org.springframework.web.util.DefaultUriBuilderFactory
    import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode


    @Configuration
    class WebClientConfig {

        @Bean
        fun seoulApiWebClient(): WebClient {
            return WebClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
                .build()
        }

        @Bean
        fun koreaDataPortalApiWebClient(): WebClient {
            val baseUrl = "https://apis.data.go.kr"
            val factory: DefaultUriBuilderFactory = DefaultUriBuilderFactory(baseUrl)
            factory.encodingMode = EncodingMode.VALUES_ONLY
            return WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(baseUrl)
                .build();
        }

        @Bean
        fun vWorldOpenApiWebClient(): WebClient {
            return WebClient.builder()
                .baseUrl("https://api.vworld.kr")
                .build()
        }

        @Bean
        fun popupApiWebClient(): WebClient {
            return WebClient.builder()
                .baseUrl("https://www.popply.co.kr/popup")
                .build()
        }
    }