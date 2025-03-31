package com.wheretopop.gateway.shared.exception

import com.wheretopop.gateway.shared.response.ErrorCode

class EntityNotFoundException(
    message: String? = ErrorCode.COMMON_ENTITY_NOT_FOUND.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.COMMON_ENTITY_NOT_FOUND
)
