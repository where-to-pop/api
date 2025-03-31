package com.wheretopop.gateway.shared.response

import com.wheretopop.gateway.shared.exception.BaseException
import com.wheretopop.gateway.shared.interceptor.CommonHttpRequestInterceptor
import org.apache.catalina.connector.ClientAbortException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.NestedExceptionUtils
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class CommonControllerAdvice {

    private val log = LoggerFactory.getLogger(CommonControllerAdvice::class.java)
    private val SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST = listOf<ErrorCode>() // 추후 추가 가능

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun onException(e: Exception): CommonResponse<Nothing> {
        val eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY)
        log.error("eventId = {}", eventId, e)
        return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BaseException::class)
    fun onBaseException(e: BaseException): CommonResponse<Nothing> {
        val eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY)
        val cause = NestedExceptionUtils.getMostSpecificCause(e)
        val logLevel = if (e.errorCode in SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST) "error" else "warn"

        when (logLevel) {
            "error" -> log.error("[BaseException] eventId = {}, cause = {}, errorMsg = {}", eventId, cause, cause.message)
            else -> log.warn("[BaseException] eventId = {}, cause = {}, errorMsg = {}", eventId, cause, cause.message)
        }

        return CommonResponse.fail(e.message, e.errorCode.name)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ClientAbortException::class)
    fun skipException(e: ClientAbortException): CommonResponse<Nothing> {
        val eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY)
        val cause = NestedExceptionUtils.getMostSpecificCause(e)
        log.warn("[skipException] eventId = {}, cause = {}, errorMsg = {}", eventId, cause, cause.message)
        return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException): CommonResponse<Nothing> {
        val eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY)
        val cause = NestedExceptionUtils.getMostSpecificCause(e)
        log.warn("[BaseException] eventId = {}, errorMsg = {}", eventId, cause.message)

        val bindingResult: BindingResult = e.bindingResult
        val fieldError: FieldError? = bindingResult.fieldError

        val message = fieldError?.let {
            "Request Error ${it.field}=${it.rejectedValue} (${it.defaultMessage})"
        } ?: ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg()

        return CommonResponse.fail(message, ErrorCode.COMMON_INVALID_PARAMETER.name)
    }
}
