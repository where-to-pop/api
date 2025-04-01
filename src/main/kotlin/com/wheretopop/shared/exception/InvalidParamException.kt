package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode


class InvalidParamException(
    message: String? = ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg(),
    errorCode: ErrorCode = ErrorCode.COMMON_INVALID_PARAMETER
) : BaseException(
    message = message,
    errorCode = errorCode
)
