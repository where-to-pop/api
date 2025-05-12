package com.wheretopop.shared.exception

import com.wheretopop.shared.response.ErrorCode

class AuthInvalidPasswordException(
    message: String? = ErrorCode.AUTH_INVALID_PASSWORD.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.AUTH_INVALID_PASSWORD
)

class AuthInvalidIdentifierException(
    message: String? = ErrorCode.AUTH_INVALID_IDENTIFIER.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.AUTH_INVALID_IDENTIFIER
)

class AuthIdentifierAlreadyExistsException(
    message: String? = ErrorCode.AUTH_IDENTIFIER_ALREADY_EXISTS.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.AUTH_IDENTIFIER_ALREADY_EXISTS
)

class AuthPasswordAlreadyExistsException(
    message: String? = ErrorCode.AUTH_PASSWORD_ALREADY_EXISTS.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.AUTH_PASSWORD_ALREADY_EXISTS
)

class AuthInvalidTokenException(
    message: String? = ErrorCode.AUTH_INVALID_TOKEN.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.AUTH_INVALID_TOKEN
)

class AuthRefreshTokenExpiredException(
    message: String? = ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED.getErrorMsg()
) : BaseException(
    message = message,
    errorCode = ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED
)