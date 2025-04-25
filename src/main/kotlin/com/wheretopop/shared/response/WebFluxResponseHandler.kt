package com.wheretopop.shared.response

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class WebFluxResponseHandler(
    configurer: ServerCodecConfigurer,
    resolver: RequestedContentTypeResolver
) : ResponseBodyResultHandler(configurer.writers, resolver) {

    override fun supports(result: HandlerResult): Boolean {
        return true
    }

    override fun handleResult(
        exchange: ServerWebExchange,
        result: HandlerResult
    ): Mono<Void> {
        val returnValue = result.returnValue
        val returnType = result.returnTypeSource as MethodParameter
        val request = exchange.request
        val response = exchange.response

        return when (returnValue) {
            is Mono<*> -> handleMono(returnValue, returnType, exchange)
            else -> super.handleResult(exchange, result)
        }
    }

    private fun handleMono(
        mono: Mono<*>,
        returnType: MethodParameter,
        exchange: ServerWebExchange
    ): Mono<Void> {
        return mono.flatMap { value ->
            when (value) {
                is CommonResponse<*> -> {
                    exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                    writeBody(value, returnType, exchange)
                }
                else -> writeBody(value, returnType, exchange)
            }
        }
    }
} 