package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode
import org.springframework.http.HttpStatus

class WhereToPoPException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.getErrorMsg(),
    val status: HttpStatus = when {
        errorCode == ErrorCode.COMMON_SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        errorCode == ErrorCode.COMMON_INVALID_PARAMETER -> HttpStatus.BAD_REQUEST
        else -> HttpStatus.OK
    }
) : RuntimeException(message)

// ErrorCode의 확장 함수로 예외를 쉽게 생성
fun ErrorCode.toException(message: String? = null) = 
    WhereToPoPException(this, message ?: this.getErrorMsg()) 