package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode


class ChatNullResponseException(
    message: String? = ErrorCode.CHAT_NULL_RESPONSE.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.CHAT_NULL_RESPONSE
)