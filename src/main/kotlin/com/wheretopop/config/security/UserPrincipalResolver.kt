package com.wheretopop.config.security

import com.wheretopop.domain.user.UserId
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

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
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map { authentication ->
                val userId = authentication.principal as? UserId
                userId?.let { UserPrincipal(it) } ?: throw IllegalStateException("User ID not found in authentication")
            }
    }
} 