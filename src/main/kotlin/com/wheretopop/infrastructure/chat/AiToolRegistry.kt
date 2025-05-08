package com.wheretopop.infrastructure.chat

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class AiToolRegistry {
    @Tool(description = "returns current local date time.")
    fun now(): String {
//        return LocalDateTime.now().toString()
        return "today is 2023-10-01 and 12:00"
    }

}