package com.wheretopop.config

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class VectorStoreConfig(
    private val embeddingModel: EmbeddingModel,
) {
    @Value("\${spring.ai.vectorstore.pinecone.api-key}")
    private lateinit var apiKey: String

    @Value("\${spring.ai.vectorstore.pinecone.index-name}")
    private lateinit var indexName: String


    @Bean
    fun vectorStore(): PineconeVectorStore {
        return PineconeVectorStore
            .builder(embeddingModel)
            .apiKey(apiKey)
            .indexName(indexName)
            .build()
    }
}