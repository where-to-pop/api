package com.wheretopop.infrastructure.chat

import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import mu.KotlinLogging
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
) : ChatAssistant {



    override fun call(prompt: Prompt, toolCallingChatOption: ToolCallingChatOptions?): ChatResponse {
        var mergedPrompt = Prompt(prompt.instructions, toolCallingChatOption)
        logger.info { "available tools: ${toolCallingChatOption?.toolCallbacks}" }
        var chatResponse = chatModel.call(mergedPrompt) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException();
        while (chatResponse.hasToolCalls()) {
            logger.info("Tool calls detected in chat response. ${chatResponse.result.output.toolCalls}")
            val toolExecutionResult: ToolExecutionResult = toolCallingManager.executeToolCalls(
                mergedPrompt,
                chatResponse
            )
            mergedPrompt = Prompt(toolExecutionResult.conversationHistory(), toolCallingChatOption)
            logger.info("Tool execution result: ${toolExecutionResult.conversationHistory()}")
            chatResponse = chatModel.call(mergedPrompt) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException();
            logger.info("chatResponse: ${chatResponse.result.output.text}")
        }
        return chatResponse
    }
}
