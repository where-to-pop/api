package com.wheretopop.application.chat

import org.springframework.ai.chat.model.ChatResponse
import reactor.core.publisher.Flux

interface McpAiUseCase {
    fun predictToolCall(query: String): Flux<ChatResponse>
    fun inferDatabaseQueryCondition(naturalQuery: String): Flux<ChatResponse>
    fun describeDatabaseSchema(naturalRequest: String): Flux<ChatResponse>
}
