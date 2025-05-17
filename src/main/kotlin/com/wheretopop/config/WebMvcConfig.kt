package com.wheretopop.config

import com.wheretopop.config.security.UserPrincipalResolver
import com.wheretopop.shared.response.WebFluxResponseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * Spring MVC 웹 설정
 * 
 * CORS 설정 및 기타 웹 관련 설정을 담당합니다.
 */
@Configuration
class WebMvcConfig : WebMvcConfigurer {
    
    /**
     * CORS 설정
     * 
     * API를 호출할 수 있는 출처(Origin)와 메서드, 헤더 등을 설정합니다.
     * 개발 환경과 프로덕션 환경에서 모두 사용할 수 있도록 구성되어 있습니다.
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                // 로컬 개발 환경
                "http://localhost:3000",
                "http://localhost:5173",
                // 개발 서버
                "https://dev.wheretopop.com",
                // 프로덕션 서버
                "https://wheretopop.com"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
            .allowCredentials(true)
            .maxAge(3600)
    }
} 