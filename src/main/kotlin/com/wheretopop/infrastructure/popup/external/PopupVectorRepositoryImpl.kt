package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupVectorRepository
import io.grpc.StatusRuntimeException
import mu.KotlinLogging
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Repository
class PopupVectorRepositoryImpl(
    @Qualifier("popupVectorStore")
    private val vectorStore: VectorStore
): PopupVectorRepository {
    override fun addPopupInfo(popupInfo: PopupInfo.Detail) {
        val document = Document(
            popupInfo.generateVectorId(),
            popupInfo.getContentForEmbedding(),
            popupInfo.buildVectorMetadataMap()
        )

        var succeeded = false
        var retries = 3
        var waitTime = 5000L

        while (retries > 0 && !succeeded) {
            try {
                vectorStore.add(listOf(document))
                succeeded = true
            } catch (e: WebClientResponseException.TooManyRequests) {
                logger.warn("429 오류. ${waitTime / 1000}초 후 재시도...")
                try {
                    TimeUnit.MILLISECONDS.sleep(waitTime)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw RuntimeException("단일 문서 처리 중 인터럽트 발생", ie)
                }
                waitTime *= 2
                retries--
                if (retries == 0) throw e
            }
        }
    }

    fun addPopupInfosInChunks(
        popupInfos: List<PopupInfo.Detail>,
        chunkSize: Int = 3,
        delayBetweenChunksMillis: Long = 6000L
    ) {
        popupInfos.chunked(chunkSize).forEachIndexed { index, chunk ->
            val documentsForChunk: List<Document> = chunk.map { info ->
                Document(
                    info.generateVectorId(),
                    info.getContentForEmbedding(),
                    info.buildVectorMetadataMap()
                )
            }

            var succeeded = false
            var retries = 3
            var waitTime = 5000L

            while (retries > 0 && !succeeded) {
                try {
                    vectorStore.add(documentsForChunk)
                    succeeded = true
                    logger.info("${chunk.size}개 문서 청크 처리 성공.")
                } catch (e: WebClientResponseException.TooManyRequests) {
                    logger.warn("429 오류. ${waitTime / 1000}초 후 재시도...")
                    try {
                        TimeUnit.MILLISECONDS.sleep(waitTime)
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw RuntimeException("청크 처리 중 인터럽트 발생", ie)
                    }
                    waitTime *= 2
                    retries--
                    if (retries == 0) throw e
                }
            }

            val isLastChunk = index == (popupInfos.chunked(chunkSize).size - 1)
            if (!isLastChunk) {
                logger.info("${delayBetweenChunksMillis / 1000}초 후 다음 청크 처리 시작...")
                try {
                    TimeUnit.MILLISECONDS.sleep(delayBetweenChunksMillis)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw RuntimeException("청크 간 대기 중 인터럽트 발생", ie)
                }
            }
        }
    }

    override fun findSimilarPopups(query: String, k: Int): List<Document> {
        val searchRequest = SearchRequest.builder().query(query).topK(k).build()
        try {
            return vectorStore.similaritySearch(searchRequest) ?: emptyList()
        } catch (e: StatusRuntimeException) {
            logger.error("Vector similarity search failed", e)
            return emptyList()
        }
    }

    override fun findById(id: Long): Document? {
        val filterExp = "original_id == $id"
        val searchRequest = SearchRequest.builder().topK(1).filterExpression(filterExp).build()
        try {
            val documents: List<Document> = vectorStore.similaritySearch(searchRequest)
            return documents.firstOrNull()
        } catch (e: StatusRuntimeException) {
            logger.error("Vector similarity search failed", e)
            return null
        }
    }

    private fun safeSearch(searchRequest: SearchRequest): List<Document> {
        return try {
            vectorStore.similaritySearch(searchRequest) ?: emptyList()
        } catch (e: StatusRuntimeException) {
            logger.error("Vector similarity search failed", e)
            emptyList()
        }
    }

    override fun findByAreaId(areaId: Long, query: String, k: Int): List<Document> {
        val filterExp = "area_id == $areaId"
        val request = SearchRequest.builder().query(query).topK(k).filterExpression(filterExp).build()
        return safeSearch(request)
    }

    override fun findByBuildingId(buildingId: Long, query: String, k: Int): List<Document> {
        val filterExp = "building_id == $buildingId"
        val request = SearchRequest.builder().query(query).topK(k).filterExpression(filterExp).build()
        return safeSearch(request)
    }

    override fun findByAreaName(areaName: String, query: String, k: Int): List<Document> {
        val filterExp = "area_name == '$areaName'"
        val request = SearchRequest.builder().query(query).filterExpression(filterExp).topK(k).build()
        return safeSearch(request)
    }

    override fun findByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<Document> {
        val filterExp = "target_age_group == '$ageGroup'"
        val request = SearchRequest.builder().query(query).filterExpression(filterExp).topK(k).build()
        return safeSearch(request)
    }

    override fun findByCategory(category: String, query: String, k: Int): List<Document> {
        val filterExp = "category == '$category'"
        val request = SearchRequest.builder().query(query).topK(k).filterExpression(filterExp).build()
        return safeSearch(request)
    }

    private fun buildFilterExpression(
        areaId: Long? = null,
        buildingId: Long? = null,
        areaName: String? = null,
        ageGroup: String? = null,
        category: String? = null,
        id: Long? = null
    ): String? {
        val filters = mutableListOf<String>()
        areaId?.let { filters.add("area_id == $it") }
        buildingId?.let { filters.add("building_id == $it") }
        areaName?.let { filters.add("area_name == '$it'") }
        ageGroup?.let { filters.add("target_age_group == '$ageGroup'") }
        category?.let { filters.add("category == '$category'") }
        return if (filters.isNotEmpty()) filters.joinToString(" && ") else null
    }

    override fun findByFilters(
        query: String,
        k: Int,
        areaId: Long?,
        buildingId: Long?,
        areaName: String?,
        ageGroup: String?,
        category: String?,
    ): List<Document> {
        val filterExp = buildFilterExpression(areaId, buildingId, areaName, ageGroup, category)
        val builder = SearchRequest.builder().query(query).topK(k)
        filterExp?.let { builder.filterExpression(it) }
        val request = builder.build()
        return safeSearch(request)
    }
}