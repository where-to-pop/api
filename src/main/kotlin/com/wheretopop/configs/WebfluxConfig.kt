package com.wheretopop.configs

import com.wheretopop.shared.response.WebFluxResponseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
class WebfluxConfig : WebFluxConfigurer {

    @Bean
    fun webFluxResponseHandler(
        configurer: ServerCodecConfigurer,
        resolver: RequestedContentTypeResolver
    ): WebFluxResponseHandler {
        return WebFluxResponseHandler(configurer, resolver)
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) // 16MB
    }
} 