package com.wheretopop.config.security

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * 현재 인증된 사용자를 주입할 수 있는 어노테이션
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentUser

/**
 * 현재 인증된 사용자의 정보를 나타내는 클래스
 */
data class UserPrincipal(
    val userId: UserId
)

/**
 * 컨트롤러 메서드에서 @CurrentUser 어노테이션이 있는 파라미터에 사용자 정보를 주입하는 리졸버
 */
@Component
class UserPrincipalResolver : HandlerMethodArgumentResolver {
    
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUser::class.java) &&
               parameter.parameterType == UserPrincipal::class.java
    }
    
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        
        if (authentication == null || !authentication.isAuthenticated) {
            throw ErrorCode.COMMON_FORBIDDEN.toException("User is not authenticated")
        }
        
        val userId = authentication.principal as? UserId
            ?: throw ErrorCode.COMMON_FORBIDDEN.toException("User ID not found in authentication")
            
        return UserPrincipal(userId)
    }
} 