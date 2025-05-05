package com.wheretopop.infrastructure.popup.external.x

import java.time.Instant


data class XResponse(
    val date: Instant,
    val content: String,
    val emotionScore: EmotionScore,
)

enum class EmotionScore(val value: Int) {
    VERY_BAD(1),
    BAD(2),
    NEUTRAL(3),
    GOOD(4),
    VERY_GOOD(5);
}