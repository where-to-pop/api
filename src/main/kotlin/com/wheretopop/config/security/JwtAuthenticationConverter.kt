package com.wheretopop.config.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

/**
 * HTTP 요청에서 JWT 토큰을 추출하고 인증 객체로 변환하는 컨버터
 */
@Component
class JwtAuthenticationConverter(
    private val jwtProvider: JwtProvider
) : ServerAuthenticationConverter {
    
    companion object {
        private const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
        private const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
        private const val USER_ID_HEADER = "X-User-Id"
        private const val DEFAULT_ROLE = "ROLE_USER"
    }
    
    /**
     * 인증 요청을 처리하여 Authentication 객체로 변환합니다.
     */
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val request = exchange.request
        
        // 액세스 토큰 추출 및 검증
        return extractAccessToken(exchange)
            ?.let { token -> 
                // 토큰이 유효한 경우에만 처리
                if (jwtProvider.validateAccessToken(token)) {
                    val userId = jwtProvider.getUserIdFromToken(token)
                    
                    if (userId != null) {
                        // 사용자 ID 헤더 추가
                        exchange.mutate().request(request.mutate()
                            .header(USER_ID_HEADER, userId.toString())
                            .build())
                            .build()
                        
                        // 기본 권한 부여
                        val authorities = Collections.singletonList(SimpleGrantedAuthority(DEFAULT_ROLE))
                        
                        // 인증 객체 생성 및 반환
                        Mono.just(UsernamePasswordAuthenticationToken(
                            userId,
                            token,
                            authorities
                        ))
                    } else {
                        Mono.empty() // 사용자 ID가 없는 경우 빈 Mono 반환
                    }
                } else {
                    Mono.empty() // 토큰이 유효하지 않은 경우 빈 Mono 반환
                }
            } ?: Mono.empty() // 토큰이 없는 경우 빈 Mono 반환
    }
    
    /**
     * 쿠키에서 액세스 토큰을 추출합니다.
     */
    private fun extractAccessToken(exchange: ServerWebExchange): String? {
        return exchange.request.cookies[ACCESS_TOKEN_COOKIE_NAME]
            ?.firstOrNull()
            ?.value
            ?.takeIf { it.isNotBlank() }
    }
    
    /**
     * 쿠키에서 리프레시 토큰을 추출합니다.
     */
    fun extractRefreshToken(exchange: ServerWebExchange): String? {
        return exchange.request.cookies[REFRESH_TOKEN_COOKIE_NAME]
            ?.firstOrNull()
            ?.value
            ?.takeIf { it.isNotBlank() }
    }
} 