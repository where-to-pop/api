package com.wheretopop.infrastructure.chat

import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import mu.KotlinLogging
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
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
//    private val chatMemory: ChatMemory
) : ChatAssistant {

    // NOTE: 현재 jdbcChatMemory 가 버그가 있어 인메모리 메모리 임시 사용
    // https://github.com/spring-projects/spring-ai/pull/3177 머지됨 릴리즈 기다리는 중
    // TODO: 릴리즈 후 메모리 변경
    private val chatMemory: ChatMemory = MessageWindowChatMemory.builder()
        .maxMessages(50)
        .build()
    override fun call(conversationId: String, prompt: Prompt, toolCallingChatOption: ToolCallingChatOptions?): ChatResponse {

        val systemMessage = prompt.systemMessage
        chatMemory.add(conversationId, prompt.userMessage)
        
        // 메모리에서 대화 이력을 가져오고 system 메시지를 추가하여 프롬프트 생성
        val promptWithMemory = Prompt(chatMemory.get(conversationId) + prompt.systemMessage, toolCallingChatOption)
        
        logger.info { "available tools: ${toolCallingChatOption?.toolCallbacks}" }
        
        // 모델 호출
        var chatResponse = chatModel.call(promptWithMemory) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        // 응답을 메모리에 추가
        chatMemory.add(conversationId, chatResponse.result.output)
        
        // 도구 호출이 있는 경우 처리
        while (chatResponse.hasToolCalls()) {
            logger.info("Tool calls detected in chat response. ${chatResponse.result.output.toolCalls}")
            
            // 도구 실행
            val toolExecutionResult: ToolExecutionResult = toolCallingManager.executeToolCalls(
                promptWithMemory,
                chatResponse
            )
            
            // 도구 실행 결과를 메모리에 추가
            val conversationHistory = toolExecutionResult.conversationHistory()
            if (conversationHistory.isNotEmpty()) {
                chatMemory.add(conversationId, conversationHistory[conversationHistory.size - 1])
            }
            
            // 업데이트된 대화 이력으로 새 프롬프트 생성
            val updatedPromptWithMemory = Prompt(chatMemory.get(conversationId) + systemMessage, toolCallingChatOption)
            
            logger.info("Tool execution result: ${toolExecutionResult.conversationHistory()}")
            
            // 새로운 응답 생성
            chatResponse = chatModel.call(updatedPromptWithMemory) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            
            // 응답을 메모리에 추가
            chatMemory.add(conversationId, chatResponse.result.output)
            
            logger.info("chatResponse: ${chatResponse.result.output.text}")
        }
        
        return chatResponse
    }
}
