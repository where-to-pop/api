package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.ChatMessageId
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.messages.ToolResponseMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.model.tool.ToolCallingManager
import org.springframework.ai.model.tool.ToolExecutionResult
import org.springframework.stereotype.Component


private val logger = KotlinLogging.logger {}

@Component
class AiChatAssistant(
    private val chatModel: ChatModel,
    private val toolCallingManager: ToolCallingManager,
) : ChatAssistant {

    override fun call(chatMessageId: ChatMessageId, prompt: Prompt, toolCallingChatOption: ToolCallingChatOptions?): ChatAssistant.ResponseWithToolExecutionResult {
        val systemMessage = prompt.systemMessage
        val conversationId = chatMessageId.toString()
        val allToolExecutionResults = mutableListOf<String>()
        
        // 한번의 호출에서 context 를 저장하기 위한 MessageWindowChatMemory 사용
        val chatMemory: MessageWindowChatMemory = MessageWindowChatMemory.builder()
            .maxMessages(20)
            .build()
        chatMemory.add(conversationId, prompt.userMessage)

        // 메모리에서 대화 이력을 가져오고 system 메시지를 추가하여 프롬프트 생성
        val promptWithMemory = Prompt(chatMemory.get(conversationId) + prompt.systemMessage, toolCallingChatOption)
        logger.info("system prompt length: ${promptWithMemory.systemMessage.text.length}")
        logger.info("User message length: ${promptWithMemory.userMessage.text.length}")
        logger.info("system prompt: ${promptWithMemory.systemMessage.text}")
        logger.info("User message: ${promptWithMemory.userMessage.text}")

        // 모델 호출 (타임아웃 설정 고려)
        var chatResponse = chatModel.call(promptWithMemory) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        logger.info("Initial generation received for conversation: $conversationId")
        
        // 응답을 메모리에 추가
        chatMemory.add(conversationId, chatResponse.result.output)

        // 도구 호출이 있는 경우 처리 (최대 반복 횟수 제한)
        var toolCallCount = 0
        val maxToolCalls = 5 // 무한 루프 방지
        
        while (chatResponse.hasToolCalls() && toolCallCount < maxToolCalls) {
            toolCallCount++
            logger.info("Tool call iteration $toolCallCount for conversation: $conversationId")
            
            // Tool call 정보 간략하게 로깅
            chatResponse.result.output.toolCalls.forEach { toolCall ->
                val functionName = toolCall.name
                val parameterKeys = toolCall.arguments ?: "no parameters"
                logger.info("Tool call: $functionName with parameters: [$parameterKeys]")
            }
            
            // 도구 실행
            val toolExecutionResult: ToolExecutionResult = toolCallingManager.executeToolCalls(
                promptWithMemory,
                chatResponse
            )
            val conversationHistory = toolExecutionResult.conversationHistory()

            // 도구 실행 결과를 수집
            val toolResultSummary = buildString {
                conversationHistory.lastOrNull()?.let { message ->
                    if (message is ToolResponseMessage) {
                        logger.info { message }
                        message.responses.forEach { response ->
                            append("###${response.name}\n");
                            append(
                                """
                                    ```
                                    ${response.responseData}
                                    ```
                                """.trimIndent()
                            )
                        }
                    }
                }
            }
            allToolExecutionResults.add(toolResultSummary)

            if (conversationHistory.isNotEmpty()) {
                val lastMessage = conversationHistory[conversationHistory.size - 1]
                chatMemory.add(conversationId, lastMessage)
            }
            
            // 업데이트된 대화 이력으로 새 프롬프트 생성
            val updatedPromptWithMemory = Prompt(chatMemory.get(conversationId) + systemMessage, toolCallingChatOption)
            logger.info("system prompt length: ${updatedPromptWithMemory.systemMessage.text.length}")
            logger.info("User message length: ${updatedPromptWithMemory.userMessage.text.length}")
            // 새로운 응답 생성
            chatResponse = chatModel.call(updatedPromptWithMemory) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            // 응답을 메모리에 추가
            chatMemory.add(conversationId, chatResponse.result.output)
        }
        
        if (toolCallCount >= maxToolCalls) {
            logger.warn("Maximum tool call iterations reached for conversation: $conversationId")
        }
        
        // 모든 도구 실행 결과를 응집하여 반환
        val aggregatedToolResults = if (allToolExecutionResults.isNotEmpty()) {
            allToolExecutionResults.joinToString("\n\n--- Next Tool Call ---\n\n")
        } else {
            null
        }
        
        return ChatAssistant.ResponseWithToolExecutionResult(
            chatResponse = chatResponse,
            toolExecutionResult = aggregatedToolResults
        )
    }

    /**
     * 스트림 기반으로 프롬프트를 처리하고 실시간 텍스트 생성 결과를 반환합니다.
     */
    override fun callStream(chatMessageId: ChatMessageId, prompt: Prompt, toolCallingChatOption: ToolCallingChatOptions?): Flow<ChatAssistant.ResponseWithToolExecutionResult> = flow {
        val conversationId = chatMessageId.toString()
        val allToolExecutionResults = mutableListOf<String>()

        val systemMessage = prompt.systemMessage
        val chatMemory: MessageWindowChatMemory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build()
        chatMemory.add(conversationId, prompt.userMessage)

        // 메모리에서 대화 이력을 가져오고 system 메시지를 추가하여 프롬프트 생성
        val promptWithMemory = Prompt(chatMemory.get(conversationId) + prompt.systemMessage, toolCallingChatOption)
        logger.info("[STREAM] system prompt length: ${promptWithMemory.systemMessage.text.length}")
        logger.info("[STREAM] User message length: ${promptWithMemory.userMessage.text.length}")
        logger.info("[STREAM] system prompt: ${promptWithMemory.systemMessage.text}")
        logger.info("[STREAM] User message: ${promptWithMemory.userMessage.text}")
        try {
            // ChatModel.stream()을 사용해 실제 텍스트 생성 스트림 받기
            val streamFlux = chatModel.stream(promptWithMemory)
            streamFlux.asFlow().collect { chatResponse ->
                // 도구 결과가 있다면 포함하여 emit
                val aggregatedToolResults = if (allToolExecutionResults.isNotEmpty()) {
                    allToolExecutionResults.joinToString("\n\n--- Next Tool Call ---\n\n")
                } else {
                    null
                }
                
                emit(ChatAssistant.ResponseWithToolExecutionResult(
                    chatResponse = chatResponse,
                    toolExecutionResult = aggregatedToolResults
                ))
            }
        } catch (e: Exception) {    
            logger.error("[스트림] Error during text streaming", e)
            
            // 폴백: 기존 방식으로 처리
            var chatResponse = chatModel.call(promptWithMemory) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            logger.info("[스트림] Fallback generation received for conversation: $conversationId")
            
            emit(ChatAssistant.ResponseWithToolExecutionResult(
                chatResponse = chatResponse,
                toolExecutionResult = null
            ))
            
            // 응답을 메모리에 추가
            chatMemory.add(conversationId, chatResponse.result.output)

            // 도구 호출이 있는 경우 처리 (최대 반복 횟수 제한)
            var toolCallCount = 0
            val maxToolCalls = 5 // 무한 루프 방지
            
            while (chatResponse.hasToolCalls() && toolCallCount < maxToolCalls) {
                toolCallCount++
                logger.info("[스트림] Tool call iteration $toolCallCount for conversation: $conversationId")
                
                // Tool call 정보 간략하게 로깅
                chatResponse.result.output.toolCalls.forEach { toolCall ->
                    val functionName = toolCall.name
                    val parameterKeys = toolCall.arguments ?: "no parameters"
                    logger.info("[스트림] Tool call: $functionName with parameters: [$parameterKeys]")
                }
                
                // 도구 실행
                val toolExecutionResult: ToolExecutionResult = toolCallingManager.executeToolCalls(
                    promptWithMemory,
                    chatResponse
                )
                
                // 도구 실행 결과를 수집
                val conversationHistory = toolExecutionResult.conversationHistory()
                val toolResultSummary = buildString {
                    conversationHistory.lastOrNull()?.let { message ->
                        if (message is ToolResponseMessage) {
                            logger.info { message }
                            message.responses.forEach { response ->
                                append("###${response.name}\n");
                                append("""
                                    ```
                                    ${response.responseData}
                                    ```
                                """.trimIndent())
                            }
                        }
                    }
                }.trimIndent()

                allToolExecutionResults.add(toolResultSummary)
                
                // 도구 실행 결과를 메모리에 추가 (결과 크기 제한)
                if (conversationHistory.isNotEmpty()) {
                    val lastMessage = conversationHistory[conversationHistory.size - 1]
                    // 메시지 크기 제한 (스트림에서는 더 작게)
                    val truncatedContent = lastMessage.text.take(1000)
                    // 새로운 메시지 생성 (copyWith 대신)
                    val truncatedMessage = UserMessage(truncatedContent)
                    chatMemory.add(conversationId, truncatedMessage)
                }
                
                // 업데이트된 대화 이력으로 새 프롬프트 생성
                val updatedPromptWithMemory = Prompt(chatMemory.get(conversationId) + systemMessage, toolCallingChatOption)
                logger.info("[스트림] Updated system prompt length: ${updatedPromptWithMemory.systemMessage.text.length}")
                logger.info("[스트림] Updated user message length: ${updatedPromptWithMemory.userMessage.text.length}")
                
                // 새로운 응답 생성
                chatResponse = chatModel.call(updatedPromptWithMemory) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
                
                // 도구 결과를 포함하여 중간 응답 방출
                val aggregatedToolResults = if (allToolExecutionResults.isNotEmpty()) {
                    allToolExecutionResults.joinToString("\n\n--- Next Tool Call ---\n\n")
                } else {
                    null
                }
                
                emit(ChatAssistant.ResponseWithToolExecutionResult(
                    chatResponse = chatResponse,
                    toolExecutionResult = aggregatedToolResults
                ))
                
                // 응답을 메모리에 추가
                chatMemory.add(conversationId, chatResponse.result.output)
                
                logger.info("[스트림] Tool call iteration $toolCallCount completed")
            }
            
            if (toolCallCount >= maxToolCalls) {
                logger.warn("[스트림] Maximum tool call iterations reached for conversation: $conversationId")
            }
        }
    }
}
