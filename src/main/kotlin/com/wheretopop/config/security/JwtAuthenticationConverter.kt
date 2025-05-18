package com.wheretopop.config.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component
import java.util.*
import jakarta.servlet.http.HttpServletRequest

/**
 * 쿠키의 JWT 토큰을 추출하고 인증 객체로 변환하는 컨버터
 */
@Component
class JwtAuthenticationConverter(
    private val jwtProvider: JwtProvider
) : AuthenticationConverter {
    
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
    override fun convert(request: HttpServletRequest): Authentication? {
        val token = extractAccessToken(request)
        
        if (token == null) {
            request.setAttribute(AUTH_STATUS, "NO_TOKEN")
            return createEmptyAuthentication()
        }

        if (!jwtProvider.validateAccessToken(token)) {
            request.setAttribute(AUTH_STATUS, "EXPIRED_TOKEN")
            return createEmptyAuthentication()
        }

        val userId = jwtProvider.getUserIdFromToken(token)
        if (userId == null) {
            request.setAttribute(AUTH_STATUS, "INVALID_TOKEN")
            return createEmptyAuthentication()
        }

        request.setAttribute(AUTH_STATUS, "VALID")
        request.setAttribute(AUTH_USER_ID, userId)

        val authorities = Collections.singletonList(SimpleGrantedAuthority(DEFAULT_ROLE))
        return UsernamePasswordAuthenticationToken(
            userId,
            token,
            authorities
        )
    }
    
    private fun createEmptyAuthentication(): Authentication {
        return UsernamePasswordAuthenticationToken(
            null,
            null,
            emptyList()
        )
    }
    
    /**
     * 쿠키에서 액세스 토큰을 추출합니다.
     */
    private fun extractAccessToken(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        
        return cookies.find { it.name == ACCESS_TOKEN_COOKIE_NAME }
            ?.value
            ?.takeIf { it.isNotBlank() }
    }
    
    /**
     * 쿠키에서 리프레시 토큰을 추출합니다.
     */
    fun extractRefreshToken(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        
        return cookies.find { it.name == REFRESH_TOKEN_COOKIE_NAME }
            ?.value
            ?.takeIf { it.isNotBlank() }
    }
} 