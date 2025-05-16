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
class SecurityConfig(
    private val corsConfiguration: CorsConfiguration
) {

    /**
     * favicon 처리를 위한 보안 필터 체인 설정
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun faviconSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            securityMatcher(ServerWebExchangeMatchers.pathMatchers("/favicon.ico"))
            
            csrf {
                disable()
            }
            
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
        }
    }
    
    /**
     * CORS preflight 요청을 위한 보안 필터 체인 설정
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    fun corsSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            // OPTIONS 메서드와 일치하는 모든 요청에 대해 적용
            securityMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**"))
            
            cors {
                configurationSource = corsConfigurationSource()
            }
            
            csrf {
                disable()
            }

            // 명시적으로 OPTIONS 요청에 대한 인증 비활성화
            httpBasic {
                disable()
            }
            formLogin {
                disable()
            }
            
            // 명시적으로 모든 프리플라이트 요청 허용
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
        }
    }

    /**
     * API 엔드포인트를 위한 보안 필터 체인 설정
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 2)
    fun apiSecurityFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationFilter: AuthenticationWebFilter
    ): SecurityWebFilterChain {
        return http {

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

            // OPTIONS 메서드는 항상 허용
            authorizeExchange {
                authorize(ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**"), permitAll)
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
     * CORS 설정 - WebfluxConfig에서 정의한 corsConfiguration 빈을 사용
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}