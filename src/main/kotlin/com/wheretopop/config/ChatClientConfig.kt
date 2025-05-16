package com.wheretopop.config

import com.wheretopop.interfaces.area.AreaToolRegistry
import io.modelcontextprotocol.client.McpAsyncClient
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.mcp.McpToolUtils
import org.springframework.ai.tool.ToolCallback
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ChatClientConfig(
    private val chatModel: ChatModel,
    private val areaToolRegistry: AreaToolRegistry,
    private val asyncClients : List<McpAsyncClient>
){

    private val logger = LoggerFactory.getLogger(ChatClientConfig::class.java)



    @Bean
    fun chatClient(): ChatClient {
        val mcpToolCallbacks: List<ToolCallback> = McpToolUtils.getToolCallbacksFromAsyncClients(asyncClients);
        return ChatClient.builder(chatModel)
            .defaultTools(areaToolRegistry, mcpToolCallbacks)
            .build()
    }
}