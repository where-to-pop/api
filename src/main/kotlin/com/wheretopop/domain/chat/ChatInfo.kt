package com.wheretopop.domain.chat

import com.wheretopop.shared.enums.ChatMessageRole
import java.time.Instant

/**
 * 도메인 계층 외부에 넘겨줄 Chat 관련 DTO 클래스들
 * 도메인 로직이 누설되지 않도록 데이터만 전달
 * ChatInfo는 data만 정의된 class로 사용, 변환의 책임은 mapper class를 활용
 */
class ChatInfo {

    /**
     * 채팅의 기본 정보를 담은 DTO
     */
    data class Main(
        val id: Long,
        val userId: Long,
        val projectId: Long,
        val isActive: Boolean,
        val title: String,
        val createdAt: Instant,
        val updatedAt: Instant
    )

    /**
     * 채팅의 모든 정보를 포함하는 DTO (모든 메시지 포함)
     */
    data class Detail(
        val id: Long,
        val userId: Long,
        val projectId: Long,
        val isActive: Boolean,
        val title: String,
        val messages: List<MessageInfo>,
        val createdAt: Instant,
        val updatedAt: Instant
    )

    /**
     * 최근 질문과 응답만 포함하는 간단한 DTO
     */
    data class Simple(
        val id: Long,
        val userId: Long,
        val projectId: Long,
        val title: String,
        val latestUserMessage: MessageInfo?,
        val latestAssistantMessage: MessageInfo?
    )

    /**
     * 채팅 메시지 정보를 담은 DTO
     */
    data class MessageInfo(
        val id: Long,
        val role: String,
        val content: String,
        val createdAt: Instant
    )
} 