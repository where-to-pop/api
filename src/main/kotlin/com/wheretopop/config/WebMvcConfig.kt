package com.wheretopop.config

import com.wheretopop.config.security.UserPrincipalResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Spring MVC 웹 설정
 * 
 * CORS 설정 및 기타 웹 관련 설정을 담당합니다.
 */
@Configuration
class WebMvcConfig(private val userPrincipalResolver: UserPrincipalResolver) : WebMvcConfigurer {
    
    /**
     * CORS 설정
     * 
     * API를 호출할 수 있는 출처(Origin)와 메서드, 헤더 등을 설정합니다.
     * 개발 환경과 프로덕션 환경에서 모두 사용할 수 있도록 구성되어 있습니다.
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(
                // 로컬 개발 환경
                "http://localhost:*",
                "https://localhost:*",
                // 프로덕션 환경
                "https://where-to-pop.devkor.club",
                "https://*.devkor.club"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Content-Type", "X-Requested-With")
            .allowCredentials(true)
            .maxAge(3600)
    }

    /**
     * 핸들러 메서드 인자 리졸버 추가
     * 
     * @CurrentUser 어노테이션이 있는 파라미터에 현재 인증된 사용자 정보를 주입합니다.
     */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userPrincipalResolver)
    }
} 