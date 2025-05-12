package com.wheretopop.config

import org.springframework.ai.document.Document
import org.springframework.ai.embedding.Embedding
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.util.Assert
import java.util.HashMap

const val GEMINI_EMBEDDING_DIMENSION = 3024

class SpringAiEmbeddingModelAdapter(
    private val geminiEmbeddingClient: GeminiEmbeddingClient,
    private val modelName: String
) : EmbeddingModel {

    override fun embed(text: String): FloatArray {
        Assert.hasText(text, "Input text must not be empty")
        val doubleList: List<Double> = geminiEmbeddingClient.embed(text)
            .block() ?: throw IllegalStateException("Embedding API call returned null or timed out for text: \"$text\"")
        return doubleList.map { it.toFloat() }.toFloatArray()
    }

    override fun embed(document: Document): FloatArray {
        Assert.notNull(document, "Input document must not be null")
        return embed(document.text)
    }

    override fun call(request: EmbeddingRequest): EmbeddingResponse {
        Assert.notNull(request, "EmbeddingRequest must not be null")
        Assert.notEmpty(request.instructions, "Input instructions (texts) must not be empty")

        val embeddings = request.instructions.mapIndexed { index, text ->
            val vector = embed(text)
            Embedding(vector, index)
        }

        return EmbeddingResponse(embeddings)
    }

    override fun dimensions(): Int {
        return GEMINI_EMBEDDING_DIMENSION
    }
}