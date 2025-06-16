package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.ChatMessageRole
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Chat 도메인 모델 테스트")
class ChatTest {

    private val userId = UserId.of(1L)
    private val projectId = ProjectId.of("1")
    private val chatId = ChatId.of(1L)

    private fun createMessage(content: String, role: ChatMessageRole): ChatMessage {
        val now = Instant.now()
        return ChatMessage.create(
            chatId = chatId,
            role = role,
            content = content,
            finishReason = null,
            latencyMs = 0L,
            createdAt = now,
            updatedAt = now,
            deletedAt = null
        )
    }

    private fun emptyChat(): Chat {
        val now = Instant.now()
        return Chat.create(
            id = chatId,
            userId = userId,
            projectId = projectId,
            isActive = true,
            title = "title",
            messages = emptyList(),
            createdAt = now,
            updatedAt = now,
            deletedAt = null
        )
    }

    @Test
    @DisplayName("메시지를 추가하면 최신 메시지로 조회된다")
    fun addMessage() {
        val chat = emptyChat()
        val message = createMessage("hello", ChatMessageRole.USER)

        val updated = chat.addMessage(message)

        assertEquals(1, updated.messages.size)
        assertEquals(message, updated.getLatestMessage())
        assertTrue(!updated.updatedAt.isBefore(chat.updatedAt))
    }

    @Test
    @DisplayName("채팅 정보를 업데이트하면 값이 변경된다")
    fun updateChatInfo() {
        val chat = emptyChat()
        val updated = chat.update(title = "new", isActive = false)

        assertEquals("new", updated.title)
        assertFalse(updated.isActive)
        assertTrue(!updated.updatedAt.isBefore(chat.updatedAt))
    }

    @Test
    @DisplayName("채팅을 삭제하면 비활성화되고 삭제 시간이 설정된다")
    fun deleteChat() {
        val chat = emptyChat()
        val deleted = chat.delete()

        assertFalse(deleted.isActive)
        assertNotNull(deleted.deletedAt)
    }

    @Test
    @DisplayName("최근 메시지 컨텍스트 문자열을 반환한다")
    fun recentMessagesAsContext() {
        val chat = emptyChat()
        val m1 = createMessage("Q1", ChatMessageRole.USER)
        val m2 = createMessage("A1", ChatMessageRole.ASSISTANT)
        val withMessages = chat.addMessage(m1).addMessage(m2)

        val context = withMessages.getRecentMessagesAsContext(2)

        assertTrue(context.contains("USER: Q1"))
        assertTrue(context.contains("ASSISTANT: A1"))
    }
}
