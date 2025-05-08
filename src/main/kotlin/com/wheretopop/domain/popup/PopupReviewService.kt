package com.wheretopop.domain.popup

import com.wheretopop.application.popup.PopupReviewUseCase
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PopupReviewService(
//    private val popupReviewRepository: PopupReviewRepository,
    private val embeddingService: TextEmbeddingService,
    private val vectorService: VectorStoreService,
): PopupReviewUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun syncReviewsToVectorDB(targetDate: LocalDate?, sourceFilter: String?) {
        logger.info("VectorDB 동기화 시작: targetDate={}, sourceFilter={}", targetDate, sourceFilter)

        val reviewsToProcess: List<PopupReview> = listOf(
            PopupReview(
                id = "1",
                reviewText = "팝업스토어 다녀왔는데 분위기 미쳤다!",
                createdAt = LocalDateTime.of(2024, 10, 28, 12, 0),
                emotionScore = "VERY_GOOD"
            ),
            PopupReview(
                id = "2",
                reviewText = "기대했던 것보단 별로였던 팝업스토어...",
                createdAt = LocalDateTime.of(2024, 9, 1, 15, 30),
                emotionScore = "BAD"
            )
        )
        if (reviewsToProcess.isEmpty()) {
            logger.info("VectorDB에 동기화할 리뷰가 없습니다.")
            return
        }
        logger.info("${reviewsToProcess.size}개의 리뷰를 VectorDB와 동기화합니다.")
        val documents: List<PopupReviewVectorDocument> = reviewsToProcess.mapNotNull { review ->
            PopupReviewVectorDocument(
                id = review.id,
                metadata = review.buildVectorMetadataMap()
            )
        }
        val testText = "Hello, world! This is an embedding test."
        logger.info("Attempting to embed text: \"{}\"", testText)

        // 임베딩 요청
        val embeddingResponse = embeddingService.embed(testText)
        if (embeddingResponse.isNotEmpty()) {
            logger.info("Successfully generated embedding for the test text.")
            logger.info("Embedding vector dimension: {}", embeddingResponse.size)
            // logger.info("First 5 dimensions: {}", embeddingResponse.take(5)) // 필요시 실제 값 일부 출력
        } else {
            logger.warn("Embedding generation returned an empty or null list.")
        }

        // 3. VectorDB에 저장
//        if (documents.isNotEmpty()) {
//            try {
//                vectorService.addDocuments(documents)
//                logger.info("${documents.size}개의 문서를 VectorDB에 성공적으로 저장했습니다.")
//            } catch (e: Exception) {
//                logger.error("VectorDB에 문서 저장 중 오류 발생", e)
//                // 부분 성공 또는 전체 실패에 대한 추가 처리 로직 고려 가능
//            }
//        }

    }
}