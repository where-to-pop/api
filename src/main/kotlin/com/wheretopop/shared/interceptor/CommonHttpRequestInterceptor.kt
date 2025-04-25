package com.wheretopop.shared.interceptor

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*

@Component
class CommonHttpRequestInterceptor : WebFilter {

    companion object {
        const val HEADER_REQUEST_UUID_KEY = "x-request-id"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response
        
        val requestEventId = request.headers.getFirst(HEADER_REQUEST_UUID_KEY)
            ?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        // MDC에 requestId 추가
        MDC.put(HEADER_REQUEST_UUID_KEY, requestEventId)
        
        // 응답 헤더에 requestId 추가
        response.headers.add(HEADER_REQUEST_UUID_KEY, requestEventId)

        return chain.filter(exchange)
            .doFinally {
                // 요청 처리 완료 후 MDC 정리
                MDC.remove(HEADER_REQUEST_UUID_KEY)
            }
    }
}
