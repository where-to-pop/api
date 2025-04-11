package com.wheretopop.domain.building

import com.wheretopop.shared.enums.FloatingPopulation
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.statistics.Hashtag
import com.wheretopop.shared.model.statistics.Keyword
import com.wheretopop.shared.model.statistics.Review
import com.wheretopop.shared.model.statistics.TransportationUsage
import java.time.LocalDateTime

/**
 * BuildingStatistic 도메인 모델
 * 건물 통계 정보를 나타내는 클래스
 */
class BuildingStatistic private constructor(
    val id: UniqueId,
    val buildingId: UniqueId,
    val collectedAt: LocalDateTime,
    val demographic: DemographicStatistic,
    val transportation: TransportationStatistic,
    val social: SocialStatistic,
    val review: ReviewStatistic
) {
    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            buildingId: UniqueId,
            collectedAt: LocalDateTime = LocalDateTime.now(),
            demographic: DemographicStatistic = DemographicStatistic(),
            transportation: TransportationStatistic = TransportationStatistic(),
            social: SocialStatistic = SocialStatistic(),
            review: ReviewStatistic = ReviewStatistic()
        ): BuildingStatistic {
            return BuildingStatistic(
                id = id,
                buildingId = buildingId,
                collectedAt = collectedAt,
                demographic = demographic,
                transportation = transportation,
                social = social,
                review = review
            )
        }
    }

    /**
     * 긍정/부정 리뷰 비율 계산
     */
    fun calculatePositiveToNegativeRatio(): Double? {
        val positive = review.positiveSentimentRatio ?: return null
        val negative = review.negativeSentimentRatio ?: return null
        
        if (negative == 0.0) return null
        return positive / negative
    }

    /**
     * 평균 평점이 특정 값 이상인지 확인
     */
    fun hasGoodRating(threshold: Double = 4.0): Boolean {
        return (review.averageRating ?: 0.0) >= threshold
    }

    /**
     * 대중교통 접근성 점수 계산 (0-100)
     * 역과의 거리가 가까울수록, 대중교통 이용률이 높을수록 높은 점수
     */
    fun calculateTransportationAccessibilityScore(): Int {
        val distance = transportation.distanceToStation ?: return 0
        val users = transportation.publicTransportUsers ?: 0
        
        // 역과의 거리 점수 (가까울수록 높음, 최대 1km까지 고려)
        val distanceScore = if (distance <= 0) 50.0 else Math.max(0.0, 50.0 * (1.0 - distance / 1000.0))
        
        // 이용객 수 점수 (많을수록 높음, 최대 5000명까지 고려)
        val userScore = Math.min(50.0, users / 100.0)
        
        return (distanceScore + userScore).toInt()
    }

    /**
     * 소셜 미디어 인기도 계산
     */
    fun calculateSocialPopularityScore(): Int {
        val mentions = social.snsMentionCount ?: 0
        val hashtagCount = social.hashtagUsageCount ?: 0
        val searchCount = social.keywordSearchCount ?: 0
        
        // 각 항목별 가중치 적용
        val mentionScore = Math.min(40, mentions / 10)
        val hashtagScore = Math.min(30, hashtagCount / 5)
        val searchScore = Math.min(30, searchCount / 20)
        
        return mentionScore + hashtagScore + searchScore
    }

    /**
     * 최근에 수집된 데이터인지 확인 (1주일 이내)
     */
    fun isRecentData(): Boolean {
        return collectedAt.isAfter(LocalDateTime.now().minusWeeks(1))
    }
}

/**
 * 인구통계 (유동인구, 방문객 등)
 */
data class DemographicStatistic(
    val totalVisitorCount: Int? = null,
    val floatingPopulation: FloatingPopulation? = null,
    val weekdayPeakHourPopulation: Int? = null,
    val weekendPeakHourPopulation: Int? = null,
    val storeCount: Int? = null
)

/**
 * 교통 통계
 */
data class TransportationStatistic(
    val distanceToStation: Double? = null,
    val publicTransportUsers: Int? = null,
    val transportationUsage: TransportationUsage? = null,
    val nearbyBusStopCount: Int? = null,
    val nearbySubwayStationCount: Int? = null
)

/**
 * 소셜 미디어 통계
 */
data class SocialStatistic(
    val snsMentionCount: Int? = null,
    val hashtagUsageCount: Int? = null,
    val keywordSearchCount: Int? = null,
    val topKeywords: List<Keyword>? = null,
    val topHashtags: List<Hashtag>? = null,
    val instagramPostCount: Int? = null,
    val newsArticleCount: Int? = null
)

/**
 * 리뷰 통계
 */
data class ReviewStatistic(
    val averageRating: Double? = null,
    val reviewCount: Int? = null,
    val positiveSentimentRatio: Double? = null,
    val negativeSentimentRatio: Double? = null,
    val recentReviews: List<Review>? = null
) 