package com.wheretopop.config.security

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.exception.WhereToPoPException
import com.wheretopop.shared.response.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * 인증 관련 유틸리티 함수
 */
object AuthUtil {
    /**
     * 현재 인증된 사용자의 ID를 추출합니다.
     * 인증되지 않은 경우 null을 반환합니다.
     */
    fun getCurrentUserId(): UserId? {
        val authentication = SecurityContextHolder.getContext().authentication
        
        if (authentication == null || !authentication.isAuthenticated || 
            authentication.principal == "anonymousUser") {
            return null
        }
        
        return authentication.principal as? UserId
    }
    
    /**
     * 현재 인증된 사용자의 ID를 추출합니다.
     * 인증되지 않은 경우 예외를 발생시킵니다.
     */
    fun getRequiredUserId(): UserId {
        return getCurrentUserId() ?: throw WhereToPoPException(ErrorCode.AUTH_ACCESS_TOKEN_NOT_FOUND)
    }
    
    /**
     * 현재 요청에서 사용자 ID를 추출합니다.
     */
    fun getUserIdFromRequest(request: HttpServletRequest): UserId? {
        val userId = request.getAttribute(JwtAuthenticationConverter.AUTH_USER_ID) as? UserId
        val authStatus = request.getAttribute(JwtAuthenticationConverter.AUTH_STATUS) as? String
        
        if (authStatus != "VALID" || userId == null) {
            return null
        }
        
        return userId
    }
    
    /**
     * 현재 요청에서 사용자 ID를 추출합니다.
     * 컨트롤러 외부에서 사용할 때 유용합니다.
     */
    fun getUserIdFromCurrentRequest(): UserId? {
        val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            ?: return null
            
        val request = requestAttributes.request
        return getUserIdFromRequest(request)
    }
}

