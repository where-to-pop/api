package com.wheretopop.config.security

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * JWT 인증된 사용자의 ID를 추출하는 확장 함수
 */
suspend fun ServerRequest.extractUserId(): UserId? {
    // 헤더에서 User-Id 확인 (JwtAuthenticationConverter에서 설정)
    val userIdHeader = this.headers().firstHeader("X-User-Id")
    if (userIdHeader != null) {
        return try {
            UserId.of(userIdHeader.toLong())
        } catch (e: Exception) {
            null
        }
    }

    // 헤더가 없는 경우 SecurityContext에서 인증 정보 추출
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
 * 인증이 필요한 GET API 엔드포인트를 위한 DSL 확장 함수
 * 인증된 사용자 ID를 쉽게 추출하여 핸들러에 전달
 */
inline fun CoRouterFunctionDsl.AUTH_GET(
    pattern: String,
    crossinline handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    GET(pattern) { request ->
        val userId = request.extractUserId() ?: run {
            return@GET ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
        
        handler(request, userId)
    }
}

/**
 * 인증이 필요한 POST API 엔드포인트를 위한 DSL 확장 함수
 */
inline fun CoRouterFunctionDsl.AUTH_POST(
    pattern: String,
    crossinline handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    POST(pattern) { request ->
        val userId = request.extractUserId() ?: run {
            return@POST ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
        
        handler(request, userId)
    }
}

/**
 * 인증이 필요한 PUT API 엔드포인트를 위한 DSL 확장 함수
 */
inline fun CoRouterFunctionDsl.AUTH_PUT(
    pattern: String,
    crossinline handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    PUT(pattern) { request ->
        val userId = request.extractUserId() ?: run {
            return@PUT ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
        
        handler(request, userId)
    }
}

/**
 * 인증이 필요한 DELETE API 엔드포인트를 위한 DSL 확장 함수
 */
inline fun CoRouterFunctionDsl.AUTH_DELETE(
    pattern: String,
    crossinline handler: suspend (ServerRequest, UserId) -> ServerResponse
) {
    DELETE(pattern) { request ->
        val userId = request.extractUserId() ?: run {
            return@DELETE ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
        
        handler(request, userId)
    }
}

