package com.wheretopop.config

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
    private val asyncClients : List<McpAsyncClient>
){
    private val logger = LoggerFactory.getLogger(ChatClientConfig::class.java)
    


    // /**
    //  * Spring AI Tool 호출을 위한 코루틴 디스패처를 제공합니다.
    //  * WebFlux 환경에서 안전하게 블로킹 작업을 수행하기 위한 별도의 스레드 풀입니다.
    //  * 모든 Tool 구현체에서 이 디스패처를 주입받아 사용할 수 있습니다.
    //  */
    // @Bean
    // fun blockingTaskExecutor(): Executor {
    //     return ThreadPoolTaskExecutor().apply {
    //         corePoolSize = 10
    //         maxPoolSize = 20
    //         queueCapacity = 100
    //         setThreadNamePrefix("blocking-task-")
    //         initialize()
    //     }
    // }
    
    // @Bean
    // fun blockingDispatcher(blockingTaskExecutor: Executor): CoroutineDispatcher {
    //     return blockingTaskExecutor.asCoroutineDispatcher()
    // }

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

}