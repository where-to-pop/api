package com.wheretopop.interfaces.area

import com.wheretopop.shared.model.statistics.*
import java.time.LocalDateTime

/**
 * Area 관련 인터페이스 레이어 DTO
 */
object AreaDto {

    /**
     * 지역 응답 DTO
     */
    data class AreaResponse(
        val id: Long,
        val name: String,
        val description: String?,
        val location: LocationResponse,
        val regionId: Long?,
//        val statistics: List<StatisticResponse>?
    )

    /**
     * 위치 정보 응답 DTO
     */
    data class LocationResponse(
        val latitude: Double,
        val longitude: Double
    )

    /**
     * 지역 통계 응답 DTO
     */
    data class StatisticResponse(
        val id: Long,
        val collectedAt: LocalDateTime,
        val commercial: CommercialResponse,
        val realEstate: RealEstateResponse,
        val social: SocialResponse,
        val demographic: DemographicResponse
    )

    /**
     * 상업 통계 응답 DTO
     */
    data class CommercialResponse(
        val storeCount: Int?,
        val newStoreCount: Int?,
        val closedStoreCount: Int?,
        val popupFrequencyCount: Int?,
        val eventCount: Int?,
        val mainStoreCategories: List<StoreCategory>?,
        val brandDistribution: List<BrandDistribution>?
    )

    /**
     * 부동산 통계 응답 DTO
     */
    data class RealEstateResponse(
        val averageRent: Long?,
        val averageVacancyRate: Double?,
        val minRent: Long?,
        val maxRent: Long?,
        val recentPriceTrend: Double?,
        val buildingCount: Int?,
        val averageBuildingAge: Double?
    )

    /**
     * 소셜 통계 응답 DTO
     */
    data class SocialResponse(
        val snsMentionCount: Int?,
        val positiveSentimentRatio: Double?,
        val negativeSentimentRatio: Double?,
        val neutralSentimentRatio: Double?,
        val topKeywords: List<Keyword>?,
        val topHashtags: List<Hashtag>?,
        val instagramPostCount: Int?,
        val blogPostCount: Int?,
        val newsArticleCount: Int?
    )

    /**
     * 인구통계 응답 DTO
     */
    data class DemographicResponse(
        val floatingPopulation: Int?,
        val populationDensityValue: Int?,
        val weekdayPeakHourPopulation: Int?,
        val weekendPeakHourPopulation: Int?,
        val ageDistribution: List<AgeDistribution>?,
        val genderRatio: List<GenderRatio>?,
        val visitorResidenceDistribution: List<VisitorResidence>?,
        val transportationUsage: List<TransportationUsage>?
    )

    /**
     * 지역 검색 요청 DTO
     */
    data class SearchRequest(
        val keyword: String? = null,
        val regionId: Long? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val radius: Double? = null,
        val minFloatingPopulation: Int? = null,
        val minStoreCount: Int? = null,
        val maxRent: Long? = null,
        val offset: Int = 0,
        val limit: Int = 20
    )
}
