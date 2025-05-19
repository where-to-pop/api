package com.wheretopop.interfaces.mcp

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class McpToolRegistry {
    @Tool(description = "returns current local date time.")
    fun now(): String {
        return "today is 2023-10-01 and 12:00"
    }

}