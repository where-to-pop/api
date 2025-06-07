package com.wheretopop.config

import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class VectorStoreConfig(
    private val embeddingModel: EmbeddingModel,
) {
    @Value("\${spring.ai.vectorstore.weaviate.scheme:http}")
    private lateinit var scheme: String

    @Value("\${spring.ai.vectorstore.weaviate.host:localhost}")
    private lateinit var host: String

    @Value("\${spring.ai.vectorstore.weaviate.port:8081}")
    private var port: Int = 0

    @Value("\${spring.ai.vectorstore.weaviate.object-class.popup:PopupStore}")
    private lateinit var popupObjectClass: String

    @Value("\${spring.ai.vectorstore.weaviate.object-class.long-term-memory:LongTermMemory}")
    private lateinit var longTermMemoryObjectClass: String

    @Bean
    fun weaviateClient(): WeaviateClient {
        val fullHost = "$host:$port"
        return WeaviateClient(Config(scheme, fullHost))
    }

    @Bean(name = ["popupVectorStore"])
    fun popupVectorStore(
        weaviateClient: WeaviateClient,
        embeddingModel: EmbeddingModel,
    ): VectorStore {
        return WeaviateVectorStore
            .builder(weaviateClient, embeddingModel)
            .objectClass(popupObjectClass)
            .filterMetadataFields(
                listOf(
                    WeaviateVectorStore.MetadataField.number("area_id"),
                    WeaviateVectorStore.MetadataField.number("building_id"),
                    WeaviateVectorStore.MetadataField.text("area_name"),
                    WeaviateVectorStore.MetadataField.text("target_age_group"),
                    WeaviateVectorStore.MetadataField.text("category"),
                )
            )
            .build()
    }

    @Bean(name = ["longTermMemoryVectorStore"])
    fun longTermMemoryVectorStore(
        weaviateClient: WeaviateClient,
        embeddingModel: EmbeddingModel,
    ): VectorStore {
        return WeaviateVectorStore
            .builder(weaviateClient, embeddingModel)
            .objectClass(longTermMemoryObjectClass)
            .build()
    }
}