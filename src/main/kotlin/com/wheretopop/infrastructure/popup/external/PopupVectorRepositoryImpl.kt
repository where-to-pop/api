package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupVectorRepository
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClientResponseException

private val logger = KotlinLogging.logger {}

@Repository
class PopupVectorRepositoryImpl(
    private val vectorStore: VectorStore
): PopupVectorRepository {
    override suspend fun addPopupInfos(popupInfos: List<PopupInfo>) {
//        val documents:List<Document> = popupInfos.map { info -> Document(
//                info.id.toString(),
//                info.getContentForEmbedding(),
//                info.buildVectorMetadataMap()
//            )
//        }
//        vectorStore.add(documents)
        addPopupInfosInChunks(popupInfos)
    }

    suspend fun addPopupInfosInChunks(
        popupInfos: List<PopupInfo>,
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
                    delay(waitTime)
                    waitTime *= 2
                    retries--
                    if (retries == 0) throw e
                }
            }

            val isLastChunk = index == (popupInfos.chunked(chunkSize).size - 1)
            if (!isLastChunk) {
                logger.info("${delayBetweenChunksMillis / 1000}초 후 다음 청크 처리 시작...")
                delay(delayBetweenChunksMillis)
            }
        }
    }

    override suspend fun findSimilarPopups(query: String): List<Document> {
        val searchRequest = SearchRequest.builder().query(query).topK(3).build()
        try {
            return vectorStore.similaritySearch(searchRequest) ?: emptyList()
        } catch (e: StatusRuntimeException) {
            logger.error("Vector similarity search failed", e)
            return emptyList()
        }
    }
}