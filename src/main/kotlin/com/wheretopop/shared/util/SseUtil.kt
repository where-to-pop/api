package com.wheretopop.shared.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.ai.chat.model.Generation
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class SseUtil {
    fun fromTextFlux(texts: Flux<String>): Flux<ServerSentEvent<String>> {
        return texts.map { text ->
            ServerSentEvent.builder(text)
                .id(UUID.randomUUID().toString())
                .event("chat-event")
                .build()
        }
    }

    fun fromGenerationFlux(generations: Flux<Generation>): Flux<ServerSentEvent<String>> {
        return generations.map { generation ->
            ServerSentEvent.builder(generation.output.text.orEmpty())
                .id(UUID.randomUUID().toString())
                .event("chat-event")
                .build()
        }
    }

    fun fromGenerationFlow(generationFlow: Flow<Generation>): Flow<ServerSentEvent<String>> {
        return generationFlow.map { generation ->
            ServerSentEvent.builder(generation.output.text.orEmpty())
                .id(UUID.randomUUID().toString())
                .event("chat-event")
                .build()
        }
    }
}
