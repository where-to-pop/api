package com.wheretopop.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

/**
 * Spring Security 설정
 * WebFlux 환경에서 JWT 인증을 처리하기 위한 설정
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {
    
    /**
     * API 엔드포인트를 위한 보안 필터 체인 설정
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun apiSecurityFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationFilter: AuthenticationWebFilter
    ): SecurityWebFilterChain {
        return http {
            securityMatcher(PathPatternParserServerWebExchangeMatcher("/v1/api/**"))
            
            cors {
                configurationSource = corsConfigurationSource()
            }
            
            csrf {
                disable()
            }
            
            httpBasic {
                disable()
            }
            formLogin {
                disable()
            }
            logout {
                disable()
            }
            
            securityContextRepository = NoOpServerSecurityContextRepository.getInstance()
            
            // 단순화된 예외 처리 방식
            exceptionHandling {
                authenticationEntryPoint = org.springframework.security.web.server.ServerAuthenticationEntryPoint { exchange, _ ->
                    Mono.fromRunnable<Void> {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    }
                }
                accessDeniedHandler = org.springframework.security.web.server.authorization.ServerAccessDeniedHandler { exchange, _ ->
                    Mono.fromRunnable<Void> {
                        exchange.response.statusCode = HttpStatus.FORBIDDEN
                    }
                }
            }
            
            // 필터 추가
            http.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            
            authorizeExchange {
                authorize(anyExchange, authenticated)
            }
        }
    }
    
    /**
     * 인증 관련 엔드포인트를 위한 보안 필터 체인 설정
     */
    @Bean
    fun authSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            // 인증 관련 경로에만 적용
            securityMatcher(PathPatternParserServerWebExchangeMatcher("/v1/auth/login", HttpMethod.POST))
            securityMatcher(PathPatternParserServerWebExchangeMatcher("/v1/auth/refresh", HttpMethod.POST))
            securityMatcher(PathPatternParserServerWebExchangeMatcher("/v1/users", HttpMethod.POST))
            
            cors {
                configurationSource = corsConfigurationSource()
            }
            
            csrf {
                disable()
            }
            
            // 단순화된 예외 처리
            exceptionHandling {
                authenticationEntryPoint = org.springframework.security.web.server.ServerAuthenticationEntryPoint { exchange, _ ->
                    Mono.fromRunnable<Void> {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    }
                }
            }
            
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
        }
    }
    
    /**
     * JWT 인증 필터 설정
     */
    @Bean
    fun jwtAuthenticationFilter(jwtAuthenticationConverter: ServerAuthenticationConverter): AuthenticationWebFilter {
        // ReactiveAuthenticationManager 구현
        val authManager = ReactiveAuthenticationManager { authentication -> Mono.just(authentication) }
        
        val filter = AuthenticationWebFilter(authManager)
        filter.setServerAuthenticationConverter(jwtAuthenticationConverter)
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange())
        return filter
    }
    
    /**
     * CORS 설정
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*") // 프로덕션에서는 특정 도메인으로 제한하세요
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
} 