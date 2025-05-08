package com.wheretopop.config

import org.springframework.ai.document.MetadataMode
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmbeddingConfig {
    @Value("\${spring.ai.vertex.ai.embedding.project-id}")
    private lateinit var projectId: String

    @Value("\${spring.ai.vertex.ai.embedding.location}")
    private lateinit var location: String

    @Value("\${spring.ai.vertex.ai.embedding.options.model}")
    private lateinit var embeddingModel: String

    @Bean
    fun connectionDetails(): VertexAiEmbeddingConnectionDetails =
        VertexAiEmbeddingConnectionDetails.builder()
            .projectId(projectId)
            .location(location)
            .build()

    @Bean
    fun embeddingOptions(): VertexAiTextEmbeddingOptions =
        VertexAiTextEmbeddingOptions.builder()
            .model(embeddingModel)
            .build()

    @Bean
    fun embeddingModel(
        connectionDetails: VertexAiEmbeddingConnectionDetails,
        options: VertexAiTextEmbeddingOptions
    ): VertexAiTextEmbeddingModel =
        VertexAiTextEmbeddingModel(connectionDetails, options)
}
