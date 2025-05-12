package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupVectorRepository
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.ai.document.Document
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

    suspend fun addPopupInfosInChunks(popupInfos: List<PopupInfo>, chunkSize: Int = 2, delayBetweenChunksMillis: Long = 5000L) {
        popupInfos.chunked(chunkSize).forEach { chunk ->
            val documentsForChunk: List<Document> = chunk.map { info ->
                Document(
                    info.id.toString(),
                    info.getContentForEmbedding(),
                    info.buildVectorMetadataMap()
                )
            }

            var Succeeded = false
            var retries = 3
            var waitTime = 1000L

            while(retries > 0 && !Succeeded) {
                try {
                    vectorStore.add(documentsForChunk)
                    Succeeded = true
                    logger.info("${chunk.size}개 문서 청크 처리 성공.")
                } catch (e: WebClientResponseException.TooManyRequests) {
                    logger.warn("429 오류. ${waitTime/1000}초 후 재시도...")
                    delay(waitTime)
                    waitTime *= 2
                    retries--
                    if(retries == 0) throw e
                }
            }

            if (popupInfos.size > chunkSize) { // 마지막 청크가 아닐 경우
                logger.info("${delayBetweenChunksMillis/1000}초 후 다음 청크 처리 시작...")
                delay(delayBetweenChunksMillis)
            }
        }
    }
}