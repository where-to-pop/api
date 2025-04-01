package com.wheretopop.gateway.shared.exception

import com.wheretopop.gateway.shared.response.ErrorCode

class SystemErrorException(
    message: String? = ErrorCode.COMMON_SYSTEM_ERROR.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.COMMON_SYSTEM_ERROR
)