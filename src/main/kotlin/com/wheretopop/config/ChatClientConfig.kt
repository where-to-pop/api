package com.wheretopop.config

import com.wheretopop.interfaces.area.AreaToolRegistry
import io.modelcontextprotocol.client.McpAsyncClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.reactor.asCoroutineDispatcher
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.mcp.McpToolUtils
import org.springframework.ai.tool.ToolCallback
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import javax.annotation.PreDestroy


@Configuration
class ChatClientConfig(
    private val chatModel: ChatModel,
    private val areaToolRegistry: AreaToolRegistry,
    private val asyncClients : List<McpAsyncClient>
){
    private val logger = LoggerFactory.getLogger(ChatClientConfig::class.java)
    
    // Reactor Scheduler 및 코루틴 디스패처 인스턴스 (모든 Tool에서 공유)
    private lateinit var toolScheduler: Scheduler
    private lateinit var toolDispatcher: CoroutineDispatcher

    /**
     * Spring AI Tool 호출을 위한 코루틴 디스패처를 제공합니다.
     * WebFlux 환경에서 안전하게 블로킹 작업을 수행하기 위한 별도의 스레드 풀입니다.
     * 모든 Tool 구현체에서 이 디스패처를 주입받아 사용할 수 있습니다.
     */
    @Bean
    fun toolDispatcher(): CoroutineDispatcher {
        toolScheduler = Schedulers.boundedElastic()
        toolDispatcher = toolScheduler.asCoroutineDispatcher()
        logger.info("Tool 호출용 코루틴 디스패처가 초기화되었습니다")
        return toolDispatcher
    }

    /**
     * AI 채팅 클라이언트를 구성합니다.
     * 모든 도구(Tool)들을 등록하여 AI가 필요에 따라 호출할 수 있게 합니다.
     */
    @Bean
    fun chatClient(): ChatClient {
        val mcpToolCallbacks: List<ToolCallback> = McpToolUtils.getToolCallbacksFromAsyncClients(asyncClients)
        logger.info("ChatClient 초기화: ${mcpToolCallbacks.size}개의 MCP 도구와 사용자 정의 도구 등록")
        
        return ChatClient.builder(chatModel)
            .defaultTools(areaToolRegistry, mcpToolCallbacks)
            .build()
    }
    
    /**
     * 애플리케이션 종료 시 리소스 정리를 수행합니다.
     */
    @PreDestroy
    fun cleanup() {
        if (::toolScheduler.isInitialized) {
            logger.info("Tool 디스패처용 Scheduler 정리 중...")
            toolScheduler.dispose()
        }
    }
}