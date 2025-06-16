package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatInfo
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.interfaces.chat.ChatController
import com.wheretopop.interfaces.chat.ChatDto
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.util.SseUtil
import com.wheretopop.config.security.UserPrincipal
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatControllerTest {
    private lateinit var chatFacade: ChatFacade
    private lateinit var sseUtil: SseUtil
    private lateinit var controller: ChatController

    @BeforeEach
    fun setUp() {
        chatFacade = mockk()
        sseUtil = mockk()
        controller = ChatController(chatFacade, sseUtil)
    }

    @Test
    @DisplayName("채팅 초기화가 성공하면 CommonResponse를 반환한다")
    fun initializeChat() {
        val principal = UserPrincipal(UserId.of(1L))
        val request = ChatDto.InitializeRequest("10", "hello")
        val chatInfo = ChatInfo.Detail(
            id = ChatId.of(1L),
            userId = principal.userId,
            projectId = ProjectId.of("10"),
            isActive = true,
            title = "title",
            messages = emptyList(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        every { chatFacade.initialize(any()) } returns chatInfo

        val response: CommonResponse<ChatDto.ChatDetailResponse> = controller.initializeChat(request, principal)

        assertEquals(CommonResponse.Result.SUCCESS, response.result)
        assertEquals("1", response.data?.id)
        assertTrue(response.data?.title == "title")
    }
}
