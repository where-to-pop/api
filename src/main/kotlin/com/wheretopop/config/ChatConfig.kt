package com.wheretopop.config

import mu.KotlinLogging
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource


@Configuration
class ChatConfig {
    private val logger = KotlinLogging.logger {}
    @Bean
    fun chatMemoryRepository(jdbcTemplate: JdbcTemplate, dataSource: DataSource): ChatMemoryRepository {
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(jdbcTemplate)
            .dialect(JdbcChatMemoryRepositoryDialect.from(dataSource))
            .build()
    }

    @Bean
    fun chatMemory(chatMemoryRepository: ChatMemoryRepository): ChatMemory {
        logger.info { "ChatMemoryRepository: $chatMemoryRepository" }
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(50)
            .build()
    }
}