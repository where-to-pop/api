package com.wheretopop.shared.enums

/**
 * 채팅 메시지의 종료 사유를 나타내는 열거형 클래스입니다.
 * @property description 종료 사유에 대한 설명
 */
enum class ChatMessageFinishReason(val description: String) {
    STOP("정지"),
    LENGTH("길이 초과"),
    CONTENT_FILTER("내용 필터링"),
    TIMEOUT("타임아웃"),
    ERROR("오류")
}