package com.wheretopop.domain.area

import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.statistics.AgeDistribution
import com.wheretopop.shared.model.statistics.BrandDistribution
import com.wheretopop.shared.model.statistics.GenderRatio
import com.wheretopop.shared.model.statistics.Hashtag
import com.wheretopop.shared.model.statistics.Keyword
import com.wheretopop.shared.model.statistics.StoreCategory
import com.wheretopop.shared.model.statistics.TransportationUsage
import com.wheretopop.shared.model.statistics.VisitorResidence
import java.time.LocalDateTime

/**
 * AreaStatistic Entity
 * Area 애그리거트의 하위 엔티티
 * 특정 지역에 대한 통계 정보를 저장
 */
class AreaStatistic private constructor(
    val id: UniqueId,
    val areaId: UniqueId, // Area의 ID
    val collectedAt: LocalDateTime,
    val commercial: CommercialStatistic,
    val realEstate: RealEstateStatistic,
    val social: SocialStatistic,
    val demographic: DemographicStatistic
) {
    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            areaId: UniqueId,
            collectedAt: LocalDateTime = LocalDateTime.now(),
            commercial: CommercialStatistic = CommercialStatistic(),
            realEstate: RealEstateStatistic = RealEstateStatistic(),
            social: SocialStatistic = SocialStatistic(),
            demographic: DemographicStatistic = DemographicStatistic()
        ): AreaStatistic {
            return AreaStatistic(
                id = id,
                areaId = areaId,
                collectedAt = collectedAt,
                commercial = commercial,
                realEstate = realEstate,
                social = social,
                demographic = demographic
            )
        }
    }

    /**
     * 인구 대비 상점 밀도 계산
     */
    fun calculateStoreDensity(): Double? {
        val population = demographic.floatingPopulation ?: return null
        val stores = commercial.storeCount ?: return null
        if (population <= 0) return null
        
        return stores.toDouble() / population * 1000 // 1000명당 상점 수
    }

    /**
     * 특정 상점 카테고리의 비율 조회
     */
    fun getCategoryPercentage(categoryName: String): Double? {
        return commercial.mainStoreCategories
            ?.find { it.name == categoryName }
            ?.percentage
    }

    /**
     * 긍정/부정 감성 비율 계산
     */
    fun calculateSentimentRatio(): Double? {
        val positive = social.positiveSentimentRatio ?: return null
        val negative = social.negativeSentimentRatio ?: return null
        if (negative == 0.0) return null
        
        return positive / negative
    }

    /**
     * 상점 변동률 계산 (신규 - 폐점) / 총 상점 수
     */
    fun calculateStoreChangeRate(): Double? {
        val total = commercial.storeCount ?: return null
        val newStores = commercial.newStoreCount ?: 0
        val closedStores = commercial.closedStoreCount ?: 0
        
        if (total == 0) return null
        return (newStores - closedStores).toDouble() / total
    }

    /**
     * 방문자 주요 거주지역 조회 (가장 높은 비율)
     */
    fun getMainVisitorResidence(): String? {
        return demographic.visitorResidenceDistribution
            ?.maxByOrNull { it.percentage }
            ?.region
    }

    /**
     * 주요 방문 연령대 조회 (상위 2개)
     */
    fun getMainVisitorAgeGroups(): List<String> {
        return demographic.ageDistribution
            ?.sortedByDescending { it.percentage }
            ?.take(2)
            ?.map { it.ageRange }
            ?: emptyList()
    }
}

/**
 * 상업 통계 (상점, 브랜드 등)
 */
data class CommercialStatistic(
    val storeCount: Int? = null,
    val newStoreCount: Int? = null,
    val closedStoreCount: Int? = null,
    val popupFrequencyCount: Int? = null,
    val eventCount: Int? = null,
    val mainStoreCategories: List<StoreCategory>? = null,
    val brandDistribution: List<BrandDistribution>? = null
)

/**
 * 부동산 통계 (임대료, 공실률 등)
 */
data class RealEstateStatistic(
    val averageRent: Long? = null,
    val averageVacancyRate: Double? = null,
    val minRent: Long? = null,
    val maxRent: Long? = null,
    val recentPriceTrend: Double? = null,
    val buildingCount: Int? = null,
    val averageBuildingAge: Double? = null
)

/**
 * 소셜 통계 (SNS 언급, 키워드 등)
 */
data class SocialStatistic(
    val snsMentionCount: Int? = null,
    val positiveSentimentRatio: Double? = null,
    val negativeSentimentRatio: Double? = null,
    val neutralSentimentRatio: Double? = null,
    val topKeywords: List<Keyword>? = null,
    val topHashtags: List<Hashtag>? = null,
    val instagramPostCount: Int? = null,
    val blogPostCount: Int? = null,
    val newsArticleCount: Int? = null
)

/**
 * 인구통계 (유동인구, 연령, 성별 등)
 */
data class DemographicStatistic(
    val floatingPopulation: Int? = null,
    val populationDensityValue: Int? = null,
    val weekdayPeakHourPopulation: Int? = null,
    val weekendPeakHourPopulation: Int? = null,
    val ageDistribution: List<AgeDistribution>? = null,
    val genderRatio: List<GenderRatio>? = null,
    val visitorResidenceDistribution: List<VisitorResidence>? = null,
    val transportationUsage: List<TransportationUsage>? = null
)
