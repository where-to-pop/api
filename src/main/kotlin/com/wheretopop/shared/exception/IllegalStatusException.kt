package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode


class IllegalStatusException(
    message: String? = ErrorCode.COMMON_ILLEGAL_STATUS.getErrorMsg(),
    errorCode: ErrorCode = ErrorCode.COMMON_ILLEGAL_STATUS
) : BaseException(
    message = message,
    errorCode = errorCode
)
