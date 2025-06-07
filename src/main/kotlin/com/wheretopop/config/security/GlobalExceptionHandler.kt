package com.wheretopop.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.wheretopop.shared.exception.WhereToPoPException
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val logger = KotlinLogging.logger {}
/**
 * Spring MVC용 글로벌 예외 핸들러 (Security 예외 처리 포함)
 */
@ControllerAdvice
class MvcGlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) {

    /**
     * Spring Security 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException) {
        logger.warn("인증 실패: ${ex.message}")
        throw ErrorCode.COMMON_FORBIDDEN.toException();
    }

    /**
     * Spring Security 인가 예외 처리 (AuthorizationDeniedException)
     */
    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException) {
        logger.warn("인가 실패: ${ex.message}")

        throw ErrorCode.COMMON_FORBIDDEN.toException();
    }

    /**
     * Spring Security 접근 거부 예외 처리 (AccessDeniedException)
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException) {
        logger.warn("접근 거부: ${ex.message}")

        throw ErrorCode.COMMON_FORBIDDEN.toException();
    }

    /**
     * WhereToPoPException 처리
     */
    @ExceptionHandler(WhereToPoPException::class)
    fun handleWhereToPoPException(ex: WhereToPoPException): ResponseEntity<CommonResponse<Any>> {
        logger.warn("비즈니스 예외 발생: ${ex.errorCode} - ${ex.message}")
        
        return ResponseEntity.status(ex.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(CommonResponse.fail(ex.errorCode))
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<CommonResponse<Any>> {
        logger.warn("잘못된 인자: ${ex.message}")
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(CommonResponse.fail(
                message = ex.message,
                errorCode = ErrorCode.COMMON_INVALID_PARAMETER.name
            ))
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<CommonResponse<Any>> {
        logger.error("예상치 못한 예외 발생", ex)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR))
    }
} 