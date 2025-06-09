package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.exception.WhereToPoPException
import com.wheretopop.shared.response.ErrorCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

@DisplayName("ChatServiceImpl 단위 테스트")
class ChatServiceImplTest {

    private lateinit var reader: ChatReader
    private lateinit var store: ChatStore
    private lateinit var scenario: ChatScenario
    private lateinit var service: ChatServiceImpl

    private val userId = UserId.of(1L)
    private val otherUserId = UserId.of(2L)
    private val projectId = ProjectId.of("1")
    private val chatId = ChatId.of(1L)

    @BeforeEach
    fun setUp() {
        reader = mockk()
        store = mockk()
        scenario = mockk()
        service = ChatServiceImpl(reader, store, scenario)
    }

    private fun baseChat(): Chat {
        val now = Instant.now()
        return Chat.create(
            id = chatId,
            userId = userId,
            projectId = projectId,
            isActive = true,
            title = "old",
            messages = emptyList(),
            createdAt = now,
            updatedAt = now,
            deletedAt = null
        )
    }

    @Test
    @DisplayName("채팅을 업데이트하면 저장 후 변경된 정보를 반환한다")
    fun updateChat() {
        val chat = baseChat()
        every { reader.findById(chatId) } returns chat
        every { store.save(any<Chat>()) } answers { firstArg() }

        val command = ChatCommand.UpdateChat(chatId, userId, "new", false)
        val result = service.updateChat(command)

        assertEquals("new", result.title)
        assertFalse(result.isActive)
        verify { store.save(any<Chat>()) }
    }

    @Test
    @DisplayName("다른 사용자가 수정 시도하면 예외가 발생한다")
    fun updateChatForbidden() {
        val chat = baseChat()
        every { reader.findById(chatId) } returns chat

        val command = ChatCommand.UpdateChat(chatId, otherUserId, "new", null)
        val ex = assertFailsWith<WhereToPoPException> { service.updateChat(command) }
        assertEquals(ErrorCode.COMMON_FORBIDDEN, ex.errorCode)
    }

    @Test
    @DisplayName("채팅을 삭제하면 비활성화되어 저장된다")
    fun deleteChat() {
        val chat = baseChat()
        every { reader.findById(chatId) } returns chat
        every { store.save(any<Chat>()) } answers { firstArg() }

        val result = service.deleteChat(ChatCommand.DeleteChat(chatId))

        assertFalse(result.isActive)
        verify { store.save(any<Chat>()) }
    }
}
