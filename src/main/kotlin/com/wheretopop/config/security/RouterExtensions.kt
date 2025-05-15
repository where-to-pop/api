package com.wheretopop.config.security

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.exception.WhereToPoPException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

/**
 * JWT 인증된 사용자의 ID를 추출하는 확장 함수
 */
suspend fun ServerRequest.extractUserId(): UserId? {
    // 헤더에서 User-Id 확인 (JwtAuthenticationConverter에서 설정)
//    val userIdHeader = this.headers().firstHeader("X-User-Id")
//    if (userIdHeader != null) {
//        return try {
//            UserId.of(userIdHeader.toLong())
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    // 헤더가 없는 경우 SecurityContext에서 인증 정보 추출
    return try {
        ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map { it.principal as? UserId }
            .awaitSingleOrNull()
    } catch (e: Exception) {
        null
    }
}

/**
 * 인증이 필요한 API 엔드포인트를 위한 DSL 확장 함수
 */
internal fun CoRouterFunctionDsl.AUTH_GET(
    pattern: String,
    handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    GET(pattern) { request ->
        checkAuthAndHandle(request, handler)
    }
}

internal fun CoRouterFunctionDsl.AUTH_POST(
    pattern: String,
    handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    POST(pattern) { request ->
        checkAuthAndHandle(request, handler)
    }
}

internal fun CoRouterFunctionDsl.AUTH_PUT(
    pattern: String,
    handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    PUT(pattern) { request ->
        checkAuthAndHandle(request, handler)
    }
}

internal fun CoRouterFunctionDsl.AUTH_DELETE(
    pattern: String,
    handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    DELETE(pattern) { request ->
        checkAuthAndHandle(request, handler)
    }
}

internal suspend fun checkAuthAndHandle(
    request: ServerRequest,
    handler: suspend (ServerRequest, UserId) -> ServerResponse
): ServerResponse {
    val status = request.exchange().attributes[JwtAuthenticationConverter.AUTH_STATUS] as? String
    val userId = request.exchange().attributes[JwtAuthenticationConverter.AUTH_USER_ID] as? UserId

    return when (status) {
        "VALID" -> {
            if (userId != null) {
                handler(request, userId)
            } else {
                throw WhereToPoPException(ErrorCode.AUTH_INVALID_TOKEN)
            }
        }
        "NO_TOKEN" -> throw WhereToPoPException(ErrorCode.AUTH_ACCESS_TOKEN_NOT_FOUND)
        "EXPIRED_TOKEN" -> throw WhereToPoPException(ErrorCode.AUTH_ACCESS_TOKEN_EXPIRED)
        "INVALID_TOKEN" -> throw WhereToPoPException(ErrorCode.AUTH_INVALID_TOKEN)
        else -> throw WhereToPoPException(ErrorCode.AUTH_INVALID_TOKEN)
    }
}

