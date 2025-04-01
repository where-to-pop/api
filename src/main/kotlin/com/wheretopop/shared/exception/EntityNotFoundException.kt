package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode


class EntityNotFoundException(
    message: String? = ErrorCode.COMMON_ENTITY_NOT_FOUND.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.COMMON_ENTITY_NOT_FOUND
)