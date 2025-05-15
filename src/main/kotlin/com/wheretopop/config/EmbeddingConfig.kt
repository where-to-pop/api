package com.wheretopop.config

import org.springframework.ai.document.MetadataMode
import org.springframework.ai.model.SimpleApiKey
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.retry.RetryUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmbeddingConfig {
    @Value("\${spring.ai.openai.embedding.base-url}")
    private lateinit var baseUrl: String

    @Value("\${spring.ai.openai.api-key}")
    private lateinit var apiKey: String

    @Value("\${spring.ai.openai.embedding.options.model}")
    private lateinit var openaiEmbeddingModel: String

    @Bean
    fun openAiApi(): OpenAiApi {
        val apiKey = SimpleApiKey(apiKey)
        return OpenAiApi.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .build()
    }

    @Bean
    fun embeddingOptions(): OpenAiEmbeddingOptions =
        OpenAiEmbeddingOptions.builder()
            .model(openaiEmbeddingModel)
            .build()

    @Bean
    fun embeddingModel(
        openAiApi: OpenAiApi,
        options: OpenAiEmbeddingOptions
    ): OpenAiEmbeddingModel =
        OpenAiEmbeddingModel(
            openAiApi,
            MetadataMode.EMBED,
            options,
            RetryUtils.DEFAULT_RETRY_TEMPLATE
        )
}