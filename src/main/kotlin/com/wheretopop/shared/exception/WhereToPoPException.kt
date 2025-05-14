package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode
import org.springframework.http.HttpStatus

class WhereToPoPException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.getErrorMsg(),
    val status: HttpStatus = when {
        errorCode.name.startsWith("AUTH_") -> HttpStatus.UNAUTHORIZED
        errorCode == ErrorCode.COMMON_ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND
        errorCode == ErrorCode.AUTH_IDENTIFIER_ALREADY_EXISTS -> HttpStatus.CONFLICT
        errorCode.name.startsWith("COMMON_") -> HttpStatus.BAD_REQUEST
        else -> HttpStatus.INTERNAL_SERVER_ERROR
    }
) : RuntimeException(message)

// ErrorCode의 확장 함수로 예외를 쉽게 생성
fun ErrorCode.toException(message: String? = null) = 
    WhereToPoPException(this, message ?: this.getErrorMsg()) 