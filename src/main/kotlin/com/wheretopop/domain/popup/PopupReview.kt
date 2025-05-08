package com.wheretopop.domain.popup

import java.time.LocalDateTime

data class PopupReview(
    val id: String, // 또는 Long, MySQL 테이블의 ID 타입에 맞게
    val reviewText: String,
    val createdAt: LocalDateTime,
    val emotionScore: String,
) {

    fun getContentForEmbedding(): String {
        return this.reviewText
    }

    fun buildVectorMetadataMap(): Map<String, Any> {
        return mapOf(
            "full_text" to this.reviewText,
            "created_at" to this.createdAt.toString(),
            "emotion_score" to this.emotionScore,
            "source" to "X_review",
            "original_db_id" to this.id
        )
    }
}