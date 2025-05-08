package com.wheretopop.shared.response

enum class ErrorCode(private val errorMsg: String) {
    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    COMMON_INVALID_PARAMETER("요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND("존재하지 않는 엔티티입니다."),
    COMMON_ILLEGAL_STATUS("잘못된 상태값입니다."),
    COMMON_NOT_IMPLEMENTED("구현되지 않은 기능입니다."),
    CHAT_NULL_RESPONSE("AI 모델에서 응답을 받지 못했습니다."),;
    fun getErrorMsg(vararg args: Any?): String {
        return if (args.isEmpty()) {
            errorMsg
        } else {
            String.format(errorMsg, *args)
        }
    }
}
