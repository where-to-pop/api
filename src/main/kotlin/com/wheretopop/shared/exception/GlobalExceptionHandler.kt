package com.wheretopop.shared.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import mu.KotlinLogging
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Configuration
@Order(-2) // 높은 우선순위로 설정
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = exchange.response
        val request = exchange.request
        
        // 예외 타입에 따른 응답 생성
        when (ex) {
            is WhereToPoPException -> {
                logger.warn {
                    """
                    |API 요청 중 예상된 예외가 발생했습니다:
                    |Path: ${request.path}
                    |Method: ${request.method}
                    |ErrorCode: ${ex.errorCode}
                    |Message: ${ex.message}
                    |Status: ${ex.status}
                    """.trimMargin()
                }
                
                response.statusCode = ex.status
                response.headers.contentType = MediaType.APPLICATION_JSON
                
                val errorResponse = CommonResponse.fail(ex.errorCode)
                val buffer: DataBuffer = response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(errorResponse)
                )
                
                return response.writeWith(Mono.just(buffer))
            }
            is IllegalArgumentException -> {
                logger.warn {
                    """
                    |API 요청 중 유효하지 않은 인자가 전달되었습니다:
                    |Path: ${request.path}
                    |Method: ${request.method}
                    |Message: ${ex.message}
                    """.trimMargin()
                }
                
                response.statusCode = HttpStatus.BAD_REQUEST
                response.headers.contentType = MediaType.APPLICATION_JSON
                
                val errorResponse = CommonResponse.fail(
                    message = ex.message,
                    errorCode = ErrorCode.COMMON_INVALID_PARAMETER.name
                )
                val buffer: DataBuffer = response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(errorResponse)
                )
                
                return response.writeWith(Mono.just(buffer))
            }
            else -> {
                logger.error(ex) {
                    """
                    |API 요청 중 예상치 못한 예외가 발생했습니다:
                    |Path: ${request.path}
                    |Method: ${request.method}
                    |Exception: ${ex.javaClass.name}
                    |Message: ${ex.message}
                    """.trimMargin()
                }
                
                response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                response.headers.contentType = MediaType.APPLICATION_JSON
                
                val errorResponse = CommonResponse.fail(
                    ErrorCode.COMMON_SYSTEM_ERROR
                )
                val buffer: DataBuffer = response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(errorResponse)
                )
                
                return response.writeWith(Mono.just(buffer))
            }
        }
    }
} 