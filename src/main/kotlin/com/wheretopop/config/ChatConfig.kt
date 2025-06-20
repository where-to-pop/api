package com.wheretopop.config

import io.modelcontextprotocol.client.McpSyncClient
import mu.KotlinLogging
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ChatConfig (
    private val mcpSyncClients: List<McpSyncClient>
) {
    private val logger = KotlinLogging.logger {}
    private val syncMcpToolCallbackProvider = SyncMcpToolCallbackProvider(mcpSyncClients)
    private val mcpToolCallbacks = syncMcpToolCallbackProvider.toolCallbacks



    @Bean
    fun chatMemory(chatMemoryRepository: ChatMemoryRepository): ChatMemory {
        logger.info { "ChatMemoryRepository: $chatMemoryRepository" }
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(50)
            .build()
    }

    @Bean
    @Qualifier("searchToolCallbacks")
    fun searchToolCallbacks(): Array<ToolCallback> {
        return mcpToolCallbacks;

    }
}