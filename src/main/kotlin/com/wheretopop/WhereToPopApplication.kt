package com.wheretopop

import com.wheretopop.interfaces.mcp.McpToolRegistry
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
class WhereToPopApplication

fun main(args: Array<String>) {

	runApplication<WhereToPopApplication>(*args)

	@Bean
	fun tools(mcpToolRegistry: McpToolRegistry?): ToolCallbackProvider {
		return MethodToolCallbackProvider.builder().toolObjects(mcpToolRegistry).build()
	}
}
