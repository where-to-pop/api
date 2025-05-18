package com.wheretopop.shared.response

enum class ErrorCode(private val errorMsg: String) {
    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    COMMON_INVALID_PARAMETER("요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND("존재하지 않는 엔티티입니다."),
    COMMON_FORBIDDEN("접근 권한이 없습니다."),
    COMMON_ILLEGAL_STATUS("잘못된 상태값입니다."),
    COMMON_NOT_IMPLEMENTED("구현되지 않은 기능입니다."),
    AUTH_INVALID_IDENTIFIER("아이디가 올바르지 않습니다."),
    AUTH_INVALID_PASSWORD("비밀번호가 올바르지 않습니다."),
    AUTH_IDENTIFIER_ALREADY_EXISTS("이미 존재하는 아이디입니다."),
    AUTH_PASSWORD_ALREADY_EXISTS("이미 존재하는 비밀번호입니다."),
    AUTH_INVALID_TOKEN("유효하지 않은 토큰입니다."),
    AUTH_ACCESS_TOKEN_NOT_FOUND("엑세스 토큰이 존재하지 않습니다."),
    AUTH_ACCESS_TOKEN_EXPIRED("엑세스 토큰이 만료되었습니다."),
    AUTH_REFRESH_TOKEN_EXPIRED("리프레시 토큰이 만료되었습니다."),
    CHAT_NULL_RESPONSE("AI 모델에서 응답을 받지 못했습니다."),;
    fun getErrorMsg(vararg args: Any?): String {
        return if (args.isEmpty()) {
            errorMsg
        } else {
            String.format(errorMsg, *args)
        }
    }
}
