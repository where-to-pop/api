package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupVectorRepository
import io.grpc.StatusRuntimeException
import mu.KotlinLogging
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Repository
class PopupVectorRepositoryImpl(
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

    override fun findSimilarPopups(query: String): List<Document> {
        val searchRequest = SearchRequest.builder().query(query).topK(3).build()
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
            logger.error("Vector similarity search failed for original_id: $id", e)
            return null
        }
    }
}