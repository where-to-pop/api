package com.wheretopop.gateway.shared.exception

import com.wheretopop.gateway.shared.response.ErrorCode

class IllegalStatusException(
    message: String? = ErrorCode.COMMON_ILLEGAL_STATUS.getErrorMsg(),
    errorCode: ErrorCode = ErrorCode.COMMON_ILLEGAL_STATUS
) : BaseException(
    message = message,
    errorCode = errorCode
)
