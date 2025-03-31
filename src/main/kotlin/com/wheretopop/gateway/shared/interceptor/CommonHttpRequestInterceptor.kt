package com.wheretopop.gateway.shared.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

@Component
class CommonHttpRequestInterceptor : HandlerInterceptor {

    companion object {
        const val HEADER_REQUEST_UUID_KEY = "x-request-id"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val requestEventId = request.getHeader(HEADER_REQUEST_UUID_KEY)
            ?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        MDC.put(HEADER_REQUEST_UUID_KEY, requestEventId)
        return true
    }
}
