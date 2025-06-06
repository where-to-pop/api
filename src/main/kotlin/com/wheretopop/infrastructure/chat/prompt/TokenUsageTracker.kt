package com.wheretopop.infrastructure.chat.prompt

import mu.KotlinLogging
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong


/**
 * 토큰 사용량 추적을 위한 클래스
 */
@Component
class TokenUsageTracker {
    private val logger = KotlinLogging.logger {}

    // 누적 토큰 사용량 추적
    private val totalPromptTokens = AtomicLong(0)
    private val totalCompletionTokens = AtomicLong(0)
    private val totalTokens = AtomicLong(0)

    /**
     * ChatResponse에서 토큰 사용량을 추출하고 로그에 기록합니다.
     */
    fun trackAndLogTokenUsage(response: ChatResponse, context: String) {
        val metadata = response.metadata

        if (metadata != null) {
            val usage = metadata.usage

            if (usage != null) {
                val promptTokens = usage.promptTokens ?: 0
                val completionTokens = usage.completionTokens ?: 0
                val totalTokens = usage.totalTokens ?: (promptTokens + completionTokens)

                // 누적 토큰 업데이트 (Int를 Long으로 변환)
                val cumulativePromptTokens = totalPromptTokens.addAndGet(promptTokens.toLong())
                val cumulativeCompletionTokens = totalCompletionTokens.addAndGet(completionTokens.toLong())
                val cumulativeTotalTokens = this.totalTokens.addAndGet(totalTokens.toLong())

                // 상세 토큰 사용량 로그
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                logger.info("🔢 토큰 사용량 추적 - $context")
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                logger.info("📝 현재 호출 토큰 사용량:")
                logger.info("   - 프롬프트 토큰: $promptTokens")
                logger.info("   - 완료 토큰: $completionTokens")
                logger.info("   - 총 토큰: $totalTokens")
                logger.info("")
                logger.info("📊 누적 토큰 사용량:")
                logger.info("   - 누적 프롬프트 토큰: $cumulativePromptTokens")
                logger.info("   - 누적 완료 토큰: $cumulativeCompletionTokens")
                logger.info("   - 누적 총 토큰: $cumulativeTotalTokens")
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            } else {
                logger.warn("⚠️ 토큰 사용량 정보가 없습니다 - $context")
            }
        } else {
            logger.warn("⚠️ 응답 메타데이터가 없습니다 - $context")
        }
    }

    /**
     * 현재까지의 누적 토큰 사용량을 반환합니다.
     */
    fun getCumulativeUsage(): TokenUsageStats {
        return TokenUsageStats(
            promptTokens = totalPromptTokens.get(),
            completionTokens = totalCompletionTokens.get(),
            totalTokens = totalTokens.get()
        )
    }

    /**
     * 토큰 사용량 통계를 초기화합니다.
     */
    fun resetUsage() {
        totalPromptTokens.set(0)
        totalCompletionTokens.set(0)
        totalTokens.set(0)
        logger.info("🔄 토큰 사용량 통계가 초기화되었습니다.")
    }
}

/**
 * 토큰 사용량 통계 데이터 클래스
 */
data class TokenUsageStats(
    val promptTokens: Long,
    val completionTokens: Long,
    val totalTokens: Long
)
