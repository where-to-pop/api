package com.wheretopop.shared.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.ai.chat.model.Generation
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
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

    /**
     * Flow<String>을 SseEmitter로 변환합니다.
     * Spring MVC에서 안전하게 SSE를 처리할 수 있습니다.
     */
    fun fromTextFlow(textFlow: Flow<String>): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE) // 타임아웃 없음
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                textFlow.collect { data ->
                    val sseEvent = SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name("react-status")
                        .data(data)
                    
                    emitter.send(sseEvent)
                }
                emitter.complete()
            } catch (e: Exception) {
                emitter.completeWithError(e)
            }
        }
        
        return emitter
    }
}
