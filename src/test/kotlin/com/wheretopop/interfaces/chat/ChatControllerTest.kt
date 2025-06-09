package com.wheretopop.interfaces.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.config.security.JwtProvider
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatInfo
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.util.SseUtil
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.Instant

@WebMvcTest(ChatController::class)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.ai.openai.api-key=test-key",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
])
@DisplayName("ChatController 테스트")
class ChatControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var chatFacade: ChatFacade

    @MockkBean
    private lateinit var sseUtil: SseUtil

    @MockkBean
    private lateinit var jwtProvider: JwtProvider

    // 테스트용 ChatInfo들
    private val testChatDetail = ChatInfo.Detail(
        id = ChatId.of(1L),
        userId = UserId.of(1L),
        projectId = ProjectId.of(100L),
        isActive = true,
        title = "테스트 채팅",
        messages = emptyList(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private val testChatMain = ChatInfo.Main(
        id = ChatId.of(1L),
        userId = UserId.of(1L),
        projectId = ProjectId.of(100L),
        isActive = true,
        title = "테스트 채팅",
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private val testChatSimple = ChatInfo.Simple(
        id = ChatId.of(1L),
        userId = UserId.of(1L),
        projectId = ProjectId.of(100L),
        title = "테스트 채팅",
        latestUserMessage = null,
        latestAssistantMessage = null
    )

    @Test
    @DisplayName("채팅 초기화 엔드포인트가 존재한다 (인증 오류)")
    fun testChatInitializeEndpointExists() {
        // Given
        val initializeRequest = mapOf(
            "projectId" to "1",
            "initialMessage" to "안녕하세요"
        )
        every { chatFacade.initialize(any()) } returns testChatDetail

        // When & Then - 인증 문제로 500 에러가 예상됨
        mockMvc.perform(post("/v1/chats")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(initializeRequest)))
            .andExpect { result ->
                val body = result.response.contentAsString
                val json = jacksonObjectMapper().readTree(body)

                assert(result.response.status == 200) { "HTTP 상태가 200이 아님. $body" }
                assert(json["result"].asText() == "FAIL") { "result != FAIL. $body" }
                assert(json["errorCode"].asText() == "COMMON_FORBIDDEN") { "에러 코드 다름. $body" }
            }

    }

    @Test
    @DisplayName("채팅 스트림 엔드포인트가 존재한다 (인증 오류)")
    fun testChatStreamEndpointExists() {
        // Given
        val mockSseEmitter = mockk<SseEmitter>()
        every { chatFacade.getChatExecutionStatusStream(any(), any(), any()) } returns flowOf("test")
        every { sseUtil.fromTextFlow(any()) } returns mockSseEmitter

        // When & Then
        mockMvc.perform(get("/v1/chats/1/stream"))
            .andExpect { result ->
                val body = result.response.contentAsString
                val json = jacksonObjectMapper().readTree(body)

                assert(result.response.status == 200) { "HTTP 상태가 200이 아님. $body" }
                assert(json["result"].asText() == "FAIL") { "result != FAIL. $body" }
                assert(json["errorCode"].asText() == "COMMON_FORBIDDEN") { "에러 코드 다름. $body" }
            }

    }

    @Test
    @DisplayName("채팅 상세 조회 엔드포인트가 존재한다 (인증 오류)")
    fun testChatDetailEndpointExists() {
        // Given
        every { chatFacade.getDetail(any()) } returns testChatDetail

        // When & Then
        mockMvc.perform(get("/v1/chats/1"))
            .andExpect { result ->
                val body = result.response.contentAsString
                val json = jacksonObjectMapper().readTree(body)

                assert(result.response.status == 200) { "HTTP 상태가 200이 아님. $body" }
                assert(json["result"].asText() == "FAIL") { "result != FAIL. $body" }
                assert(json["errorCode"].asText() == "COMMON_FORBIDDEN") { "에러 코드 다름. $body" }
            }

    }

    @Test
    @DisplayName("채팅 목록 조회 엔드포인트가 존재한다 (인증 오류)")
    fun testChatListEndpointExists() {
        // Given
        every { chatFacade.getList(any()) } returns listOf(testChatMain)

        // When & Then
        mockMvc.perform(get("/v1/chats"))
            .andExpect { result ->
                val body = result.response.contentAsString
                val json = jacksonObjectMapper().readTree(body)

                assert(result.response.status == 200) { "HTTP 상태가 200이 아님. $body" }
                assert(json["result"].asText() == "FAIL") { "result != FAIL. $body" }
                assert(json["errorCode"].asText() == "COMMON_FORBIDDEN") { "에러 코드 다름. $body" }
            }
    }

    @Test
    @DisplayName("잘못된 JSON 형식 채팅 요청이 500 에러를 반환한다")
    fun testInvalidJsonChatRequest() {
        // Given - 잘못된 JSON
        val malformedJson = "{ invalid json }"

        // When & Then - JSON 파싱 에러가 시스템 에러로 처리됨
        mockMvc.perform(post("/v1/chats")
            .contentType(MediaType.APPLICATION_JSON)
            .content(malformedJson))
            .andExpect(status().isInternalServerError) // 실제로는 500으로 응답
    }

    @Test
    @DisplayName("ChatController가 올바른 RequestMapping을 가진다")
    fun testControllerRequestMapping() {
        // Given & When
        val requestMappingAnnotation = ChatController::class.java
            .getAnnotation(org.springframework.web.bind.annotation.RequestMapping::class.java)
        
        // Then
        assert(requestMappingAnnotation != null)
        assert(requestMappingAnnotation.value.contains("/v1/chats"))
    }

    @Test
    @DisplayName("ChatController가 RestController로 등록되어 있다")
    fun testRestControllerAnnotation() {
        // Given & When
        val restControllerAnnotation = ChatController::class.java
            .getAnnotation(org.springframework.web.bind.annotation.RestController::class.java)
        
        // Then
        assert(restControllerAnnotation != null)
    }
} 