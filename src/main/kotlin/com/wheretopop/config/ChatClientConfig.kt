package com.wheretopop.config

import com.wheretopop.interfaces.mcp.McpToolRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ChatClientConfig(
    private val chatModel: ChatModel,
    private val mcpToolRegistry: McpToolRegistry,
//    private val toolCallbackProvider: ToolCallbackProvider,
){

    private val logger = LoggerFactory.getLogger(ChatClientConfig::class.java)



    @Bean
    fun chatClient(): ChatClient {
 //        val defaultCallbacks = toolCallbackProvider.toolCallbacks
//        val selfHostedCallbacks = MethodToolCallbackProvider.builder()
//            .toolObjects(mcpToolRegistry)
//            .build()
//            .toolCallbacks
//        val tool = McpToolUtils.toAsyncToolSpecifications(*defaultCallbacks, *selfHostedCallbacks)


        return ChatClient.builder(chatModel)
            .defaultTools(mcpToolRegistry)
            .build()
    }
}