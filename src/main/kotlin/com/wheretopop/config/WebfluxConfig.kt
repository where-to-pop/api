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

@Configuration
@EnableWebFlux
class WebfluxConfig(
    private val userPrincipalResolver: UserPrincipalResolver
) : WebFluxConfigurer {

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

    /**
     * 커스텀 ArgumentResolver 등록
     */
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userPrincipalResolver)
    }
    
    /**
     * 정적 리소스 핸들러 설정 - favicon.ico 처리
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(org.springframework.http.CacheControl.noCache())
    }

    /**
     * 공통 CORS 설정을 정의하는 빈
     */
    @Bean
    fun corsConfiguration(): CorsConfiguration {
        val corsConfig = CorsConfiguration()
        

        // 특정 오리진 허용
        corsConfig.allowedOrigins = listOf(
            "https://wheretopop.devkor.club", 
            "https://www.wheretopop.devkor.club", 
            "http://localhost:3000",
        )
        
        corsConfig.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
        corsConfig.allowedHeaders = listOf("*")
        corsConfig.exposedHeaders = listOf("Authorization", "Content-Type", "Set-Cookie", "X-Requested-With", "Access-Control-Allow-Origin")
        corsConfig.allowCredentials = true
        corsConfig.maxAge = 3600L
        return corsConfig
    }

    /**
     * CORS를 처리하기 위한 웹 필터
     */
    @Bean
    fun corsWebFilter(corsConfiguration: CorsConfiguration): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return CorsWebFilter(source)
    }
} 