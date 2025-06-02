package com.wheretopop.shared.response

data class CommonResponse<T>(
    val result: Result,
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null
) {
    enum class Result {
        SUCCESS, FAIL
    }

    companion object {
        @JvmStatic
        fun <T> success(data: T, message: String? = null): CommonResponse<T> =
            CommonResponse(Result.SUCCESS, data, message)

        @JvmStatic
        fun fail(message: String?, errorCode: String?): CommonResponse<Any> =
            CommonResponse(Result.FAIL, null, message, errorCode)

        @JvmStatic
        fun fail(errorCode: ErrorCode): CommonResponse<Any> =
            CommonResponse(Result.FAIL, null, errorCode.getErrorMsg(), errorCode.name)
    }
}
