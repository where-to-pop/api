package com.wheretopop.config

import org.slf4j.LoggerFactory
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

data class TextPart(val text: String)
data class EmbeddingContent(val parts: List<TextPart>)
data class GeminiEmbeddingRequest(
    val model: String,
    val content: EmbeddingContent
)
data class EmbeddingVector(val values: List<Double>)
data class GeminiEmbeddingResponse(val embedding: EmbeddingVector)

class GeminiEmbeddingClient(
    private val webClient: WebClient,
    private val apiKey: String,
    private val modelName: String
) {
    private val logger = LoggerFactory.getLogger(GeminiEmbeddingClient::class.java)

    fun embed(textToEmbed: String): Mono<List<Double>> {
        val requestModelIdentifier = "models/$modelName"

        val requestPayload = GeminiEmbeddingRequest(
            model = requestModelIdentifier,
            content = EmbeddingContent(parts = listOf(TextPart(text = textToEmbed)))
        )

        val apiPath = "/$modelName:embedContent"

        return webClient.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path(apiPath)
                    .queryParam("key", apiKey)
                    .build()
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestPayload)
            .retrieve()
            .bodyToMono(GeminiEmbeddingResponse::class.java)
            .map { response -> response.embedding.values }
            .doOnError { error ->
                logger.error("Error calling Gemini Embedding API for model '$modelName'. URI: '$apiPath', Error: ${error.message}", error)
            }
    }
}

@Configuration
class EmbeddingConfig {

    @Value("\${spring.ai.openai.api-key}")
    private lateinit var apiKey: String

    @Value("\${spring.ai.openai.embedding.base-url}")
    private lateinit var baseUrl: String

    @Value("\${spring.ai.openai.embedding.options.model:gemini-embedding-exp-03-07}")
    private lateinit var embeddingModelName: String

    @Bean
    fun geminiApiWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun geminiEmbeddingClient(geminiApiWebClient: WebClient): GeminiEmbeddingClient {
        return GeminiEmbeddingClient(
            webClient = geminiApiWebClient,
            apiKey = apiKey,
            modelName = embeddingModelName,
        )
    }

    @Bean
    fun embeddingModel(client: GeminiEmbeddingClient): EmbeddingModel {
        return SpringAiEmbeddingModelAdapter(client, embeddingModelName)
    }
}