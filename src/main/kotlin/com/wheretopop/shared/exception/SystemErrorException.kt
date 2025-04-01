package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode


class SystemErrorException(
    message: String? = ErrorCode.COMMON_SYSTEM_ERROR.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.COMMON_SYSTEM_ERROR
)