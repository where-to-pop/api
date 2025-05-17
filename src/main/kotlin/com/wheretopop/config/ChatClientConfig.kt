package com.wheretopop.config

import io.modelcontextprotocol.client.McpAsyncClient
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.mcp.McpToolUtils
import org.springframework.ai.tool.ToolCallback
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import javax.annotation.PreDestroy

@Configuration
class ChatClientConfig(
    private val chatModel: ChatModel,
    private val asyncClients: List<McpAsyncClient>
) {
    private val logger = LoggerFactory.getLogger(ChatClientConfig::class.java)
    
    // 도구 실행을 위한 자바 스레드풀
    private val toolExecutor = Executors.newFixedThreadPool(10)

    /**
     * AI 채팅 클라이언트를 구성합니다.
     * 모든 도구(Tool)들을 등록하여 AI가 필요에 따라 호출할 수 있게 합니다.
     */
    @Bean
    fun chatClient(): ChatClient {
        val mcpToolCallbacks: List<ToolCallback> = McpToolUtils.getToolCallbacksFromAsyncClients(asyncClients)
        logger.info("ChatClient 초기화: ${mcpToolCallbacks.size}개의 MCP 도구와 사용자 정의 도구 등록")
        
        return ChatClient.builder(chatModel)
            .defaultTools(mcpToolCallbacks)
            .build()
    }
    
    /**
     * 애플리케이션 종료 시 리소스 정리를 수행합니다.
     */
    @PreDestroy
    fun cleanup() {
        if (!toolExecutor.isShutdown) {
            logger.info("Tool 실행용 Executor 정리 중...")
            toolExecutor.shutdown()
        }
    }
    
    /**
     * 도구 실행에 사용할 스레드풀을 제공합니다.
     */
    @Bean
    fun toolExecutor() = toolExecutor
}