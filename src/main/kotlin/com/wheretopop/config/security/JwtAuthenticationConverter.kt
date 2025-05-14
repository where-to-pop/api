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
 * 쿠키의 JWT 토큰을 추출하고 인증 객체로 변환하는 컨버터
 */
@Component
class JwtAuthenticationConverter(
    private val jwtProvider: JwtProvider
) : ServerAuthenticationConverter {
    
    companion object {
        private const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
        private const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
        private const val DEFAULT_ROLE = "ROLE_USER"
        const val AUTH_STATUS = "auth_status"
        const val AUTH_USER_ID = "auth_user_id"
    }
    
    /**
     * 인증 요청을 처리하여 Authentication 객체로 변환합니다.
     */
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val token = extractAccessToken(exchange)
        
        if (token == null) {
            return createEmptyAuthentication(exchange, "NO_TOKEN")
        }

        if (!jwtProvider.validateAccessToken(token)) {
            return createEmptyAuthentication(exchange, "EXPIRED_TOKEN")
        }

        val userId = jwtProvider.getUserIdFromToken(token)
        if (userId == null) {
            return createEmptyAuthentication(exchange, "INVALID_TOKEN")
        }

        exchange.attributes[AUTH_STATUS] = "VALID"
        exchange.attributes[AUTH_USER_ID] = userId

        val authorities = Collections.singletonList(SimpleGrantedAuthority(DEFAULT_ROLE))
        return Mono.just(UsernamePasswordAuthenticationToken(
            userId,
            token,
            authorities
        ))
    }
    
    private fun createEmptyAuthentication(exchange: ServerWebExchange, status: String): Mono<Authentication> {
        exchange.attributes[AUTH_STATUS] = status
        return Mono.just(UsernamePasswordAuthenticationToken(
            null,
            null,
            emptyList()
        ))
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