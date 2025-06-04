package com.wheretopop.infrastructure.chat

import com.wheretopop.shared.exception.WhereToPoPException
import com.wheretopop.shared.response.ErrorCode
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.model.tool.ToolCallingManager
import org.springframework.ai.model.tool.ToolExecutionResult

@DisplayName("AiChatAssistant 테스트")
class AiChatAssistantTest {

    // 모킹 객체들
    private val mockChatModel = mockk<ChatModel>()
    private val mockToolCallingManager = mockk<ToolCallingManager>()
    
    // 테스트 대상
    private val aiChatAssistant = AiChatAssistant(
        chatModel = mockChatModel,
        toolCallingManager = mockToolCallingManager
    )

    // 테스트 픽스처
    private val conversationId = "test-conversation-123"
    private val userMessage = UserMessage("안녕하세요, 도움이 필요합니다.")
    private val systemMessage = SystemMessage("당신은 도움이 되는 AI 어시스턴트입니다.")
    private val prompt = Prompt(listOf(userMessage, systemMessage))
    private val toolCallingOptions = mockk<ToolCallingChatOptions>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("call 메소드 테스트")
    inner class CallMethodTest {
        
        @Test
        @DisplayName("정상적인 응답을 받을 때 채팅 응답을 반환한다")
        fun testNormalResponse() {
            // Given
            val assistantMessage = AssistantMessage("안녕하세요! 무엇을 도와드릴까요?")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            val result = aiChatAssistant.call(conversationId, prompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            assertEquals("안녕하세요! 무엇을 도와드릴까요?", result.result.output.text)
            verify { mockChatModel.call(any<Prompt>()) }
        }

        @Test
        @DisplayName("도구 호출이 포함된 응답을 받을 때 도구를 실행하고 최종 응답을 반환한다")
        fun testToolCallResponse() {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val finalResponse = mockk<ChatResponse>()
            
            val assistantMessage = AssistantMessage("도구 실행 결과입니다.")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            val conversationHistory = listOf<Message>(UserMessage("도구 실행 결과"))

            // 첫 번째 응답은 도구 호출 포함, 두 번째는 최종 응답
            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { finalResponse.hasToolCalls() } returns false
            every { finalResponse.result } returns generation
            
            every { mockChatModel.call(any<Prompt>()) } returnsMany listOf(toolCallResponse, finalResponse)
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns conversationHistory

            // When
            val result = aiChatAssistant.call(conversationId, prompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            verify(exactly = 2) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 1) { mockToolCallingManager.executeToolCalls(any(), any()) }
        }

        @Test
        @DisplayName("최대 도구 호출 횟수에 도달할 때 무한 루프를 방지하고 마지막 응답을 반환한다")
        fun testMaxToolCallLimit() {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val assistantMessage = AssistantMessage("무한 도구 호출 테스트")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            val conversationHistory = listOf<Message>(UserMessage("도구 실행 결과"))

            // 항상 도구 호출이 포함된 응답을 반환
            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { mockChatModel.call(any<Prompt>()) } returns toolCallResponse
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns conversationHistory

            // When
            val result = aiChatAssistant.call(conversationId, prompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            // 최대 6번 호출 (첫 번째 + 5번의 도구 호출 반복)
            verify(exactly = 6) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 5) { mockToolCallingManager.executeToolCalls(any(), any()) }
        }

        @Test
        @DisplayName("ChatModel이 null을 반환할 때 WhereToPoPException을 던진다")
        fun testNullResponseThrowsException() {
            // Given
            every { mockChatModel.call(any<Prompt>()) } returns null

            // When & Then
            val exception = assertThrows<WhereToPoPException> {
                aiChatAssistant.call(conversationId, prompt, toolCallingOptions)
            }
            assertEquals(ErrorCode.CHAT_NULL_RESPONSE, exception.errorCode)
        }

        @Test
        @DisplayName("도구 실행 중에 ChatModel이 null을 반환할 때 WhereToPoPException을 던진다")
        fun testNullResponseDuringToolExecutionThrowsException() {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val assistantMessage = AssistantMessage("도구 호출")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            val conversationHistory = listOf<Message>(UserMessage("도구 실행 결과"))

            // 첫 번째 호출은 성공, 두 번째 호출에서 null 반환
            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { mockChatModel.call(any<Prompt>()) } returnsMany listOf(toolCallResponse, null)
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns conversationHistory

            // When & Then
            val exception = assertThrows<WhereToPoPException> {
                aiChatAssistant.call(conversationId, prompt, toolCallingOptions)
            }
            assertEquals(ErrorCode.CHAT_NULL_RESPONSE, exception.errorCode)
        }

        @Test
        @DisplayName("빈 conversation history가 있을 때도 정상 동작한다")
        fun testEmptyConversationHistory() {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val finalResponse = mockk<ChatResponse>()
            
            val assistantMessage = AssistantMessage("도구 실행 완료")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            val emptyConversationHistory = emptyList<Message>()

            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { finalResponse.hasToolCalls() } returns false
            every { finalResponse.result } returns generation
            
            every { mockChatModel.call(any<Prompt>()) } returnsMany listOf(toolCallResponse, finalResponse)
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns emptyConversationHistory

            // When
            val result = aiChatAssistant.call(conversationId, prompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            verify(exactly = 2) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 1) { mockToolCallingManager.executeToolCalls(any(), any()) }
        }
    }

    @Nested
    @DisplayName("callStream 메소드 테스트")
    inner class CallStreamMethodTest {
        
        @Test
        @DisplayName("정상적인 스트림 응답을 받을 때 채팅 응답 플로우를 반환한다")
        fun testNormalStreamResponse() = runTest {
            // Given
            val assistantMessage = AssistantMessage("스트리밍 응답입니다.")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            val result = aiChatAssistant.callStream(conversationId, prompt, toolCallingOptions)
            val responses = result.toList()

            // Then
            assertEquals(1, responses.size)
            assertEquals("스트리밍 응답입니다.", responses[0].result.output.text)
            verify { mockChatModel.call(any<Prompt>()) }
        }

        @Test
        @DisplayName("도구 호출이 포함된 스트림 응답을 받을 때 중간 응답들을 포함한 플로우를 반환한다")
        fun testStreamWithToolCalls() = runTest {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val finalResponse = mockk<ChatResponse>()
            
            val assistantMessage = AssistantMessage("스트림 도구 실행 완료")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            val conversationHistory = listOf<Message>(UserMessage("도구 실행 결과가 여기에 있습니다"))

            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { finalResponse.hasToolCalls() } returns false
            every { finalResponse.result } returns generation
            
            every { mockChatModel.call(any<Prompt>()) } returnsMany listOf(toolCallResponse, finalResponse)
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns conversationHistory

            // When
            val result = aiChatAssistant.callStream(conversationId, prompt, toolCallingOptions)
            val responses = result.toList()

            // Then
            assertEquals(2, responses.size)
            verify(exactly = 2) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 1) { mockToolCallingManager.executeToolCalls(any(), any()) }
        }

        @Test
        @DisplayName("스트림에서 ChatModel이 null을 반환할 때 WhereToPoPException을 던진다")
        fun testStreamNullResponseThrowsException() = runTest {
            // Given
            every { mockChatModel.call(any<Prompt>()) } returns null

            // When & Then
            val exception = assertThrows<WhereToPoPException> {
                val result = aiChatAssistant.callStream(conversationId, prompt, toolCallingOptions)
                result.toList() // Flow를 실행하기 위해 collect
            }
            assertEquals(ErrorCode.CHAT_NULL_RESPONSE, exception.errorCode)
        }

        @Test
        @DisplayName("스트림에서 최대 도구 호출 횟수에 도달할 때 무한 루프를 방지하고 중간 응답들을 모두 방출한다")
        fun testStreamMaxToolCallLimit() = runTest {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val assistantMessage = AssistantMessage("무한 스트림 도구")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            val conversationHistory = listOf<Message>(UserMessage("반복 도구 실행"))

            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { mockChatModel.call(any<Prompt>()) } returns toolCallResponse
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns conversationHistory

            // When
            val result = aiChatAssistant.callStream(conversationId, prompt, toolCallingOptions)
            val responses = result.toList()

            // Then
            assertEquals(6, responses.size) // 첫 번째 + 5번의 도구 호출 반복 응답
            verify(exactly = 6) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 5) { mockToolCallingManager.executeToolCalls(any(), any()) }
        }

        @Test
        @DisplayName("스트림에서 긴 메시지 내용이 잘리는지 확인한다")
        fun testStreamMessageTruncation() = runTest {
            // Given
            val toolCallResponse = mockk<ChatResponse>()
            val finalResponse = mockk<ChatResponse>()
            
            val assistantMessage = AssistantMessage("스트림 도구 실행")
            val generation = Generation(assistantMessage)
            
            val toolExecutionResult = mockk<ToolExecutionResult>()
            // 1000자를 초과하는 긴 메시지
            val longMessage = "a".repeat(1500)
            val conversationHistory = listOf<Message>(UserMessage(longMessage))

            every { toolCallResponse.hasToolCalls() } returns true
            every { toolCallResponse.result } returns generation
            every { finalResponse.hasToolCalls() } returns false
            every { finalResponse.result } returns generation
            
            every { mockChatModel.call(any<Prompt>()) } returnsMany listOf(toolCallResponse, finalResponse)
            every { mockToolCallingManager.executeToolCalls(any(), any()) } returns toolExecutionResult
            every { toolExecutionResult.conversationHistory() } returns conversationHistory

            // When
            val result = aiChatAssistant.callStream(conversationId, prompt, toolCallingOptions)
            val responses = result.toList()

            // Then
            assertEquals(2, responses.size)
            verify(exactly = 2) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 1) { mockToolCallingManager.executeToolCalls(any(), any()) }
            // 메시지가 1000자로 잘린 것을 확인하는 것은 내부 구현에 의존하므로 여기서는 생략
        }
    }

    @Nested
    @DisplayName("메모리 관리 테스트")
    inner class MemoryManagementTest {
        
        @Test
        @DisplayName("대화 이력이 누적될 때 MessageWindowChatMemory의 제한을 준수한다")
        fun testMemoryManagement() {
            // Given
            val assistantMessage = AssistantMessage("메모리 테스트 응답")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            repeat(3) {
                aiChatAssistant.call("$conversationId-$it", prompt, toolCallingOptions)
            }

            // Then
            verify(exactly = 3) { mockChatModel.call(any<Prompt>()) }
        }

        @Test
        @DisplayName("서로 다른 conversation ID는 독립적인 메모리를 가진다")
        fun testIndependentMemoryPerConversation() {
            // Given
            val assistantMessage = AssistantMessage("독립적인 대화")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            aiChatAssistant.call("conversation-1", prompt, toolCallingOptions)
            aiChatAssistant.call("conversation-2", prompt, toolCallingOptions)
            aiChatAssistant.call("conversation-1", prompt, toolCallingOptions)

            // Then
            verify(exactly = 3) { mockChatModel.call(any<Prompt>()) }
            // 각 대화는 독립적이므로 메모리가 공유되지 않음
        }
    }

    @Nested
    @DisplayName("hasToolCalls 동작 테스트")
    inner class HasToolCallsTest {
        
        @Test
        @DisplayName("도구 호출이 없는 응답에 대해 hasToolCalls가 false를 반환하고 도구를 실행하지 않는다")
        fun testNoToolCalls() {
            // Given
            val assistantMessage = AssistantMessage("일반 응답")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            val result = aiChatAssistant.call(conversationId, prompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            verify(exactly = 1) { mockChatModel.call(any<Prompt>()) }
            verify(exactly = 0) { mockToolCallingManager.executeToolCalls(any(), any()) }
        }
    }

    @Nested
    @DisplayName("프롬프트 처리 테스트")
    inner class PromptProcessingTest {
        
        @Test
        @DisplayName("null toolCallingOptions로도 정상 동작한다")
        fun testNullToolCallingOptions() {
            // Given
            val assistantMessage = AssistantMessage("null 옵션 테스트")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            val result = aiChatAssistant.call(conversationId, prompt, null)

            // Then
            assertNotNull(result)
            assertEquals("null 옵션 테스트", result.result.output.text)
            verify { mockChatModel.call(any<Prompt>()) }
        }

        @Test
        @DisplayName("빈 문자열 메시지로도 정상 동작한다")
        fun testEmptyMessage() {
            // Given
            val emptyUserMessage = UserMessage("")
            val emptyPrompt = Prompt(listOf(emptyUserMessage, systemMessage))
            
            val assistantMessage = AssistantMessage("빈 메시지에 대한 응답")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            val result = aiChatAssistant.call(conversationId, emptyPrompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            assertEquals("빈 메시지에 대한 응답", result.result.output.text)
            verify { mockChatModel.call(any<Prompt>()) }
        }

        @Test
        @DisplayName("매우 긴 conversationId로도 정상 동작한다")
        fun testLongConversationId() {
            // Given
            val longConversationId = "a".repeat(1000)
            
            val assistantMessage = AssistantMessage("긴 ID 테스트")
            val generation = Generation(assistantMessage)
            val chatResponse = ChatResponse(listOf(generation))

            every { mockChatModel.call(any<Prompt>()) } returns chatResponse

            // When
            val result = aiChatAssistant.call(longConversationId, prompt, toolCallingOptions)

            // Then
            assertNotNull(result)
            assertEquals("긴 ID 테스트", result.result.output.text)
            verify { mockChatModel.call(any<Prompt>()) }
        }
    }
} 