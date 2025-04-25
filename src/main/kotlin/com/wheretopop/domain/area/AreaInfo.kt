package com.wheretopop.domain.area

import com.wheretopop.shared.model.statistics.*
import java.time.LocalDateTime

/**
 * 도메인 계층 외부에 넘겨줄 Area 관련 DTO 클래스들
 * 도메인 로직이 누설되지 않도록 데이터만 전달
 */
class AreaInfo {

    /**
     * Area 기본 정보를 담은 DTO
     */
    data class Main(
        val id: Long,
        val name: String,
        val description: String?,
        val location: LocationInfo,
        val regionId: Long?,
    )
    
    /**
     * Location 정보를 담은 DTO
     */
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double
    )

    /**
     * Area 통계 정보를 담은 DTO
     */
    data class StatisticInfo(
        val id: Long,
        val collectedAt: LocalDateTime,
        val commercial: CommercialInfo,
        val realEstate: RealEstateInfo,
        val social: SocialInfo,
        val demographic: DemographicInfo
    )
    
    /**
     * 상업 통계 정보 DTO
     */
    data class CommercialInfo(
        val storeCount: Int?,
        val newStoreCount: Int?,
        val closedStoreCount: Int?,
        val popupFrequencyCount: Int?,
        val eventCount: Int?,
        val mainStoreCategories: List<StoreCategory>?,
        val brandDistribution: List<BrandDistribution>?
    )
    
    /**
     * 부동산 통계 정보 DTO
     */
    data class RealEstateInfo(
        val averageRent: Long?,
        val averageVacancyRate: Double?,
        val minRent: Long?,
        val maxRent: Long?,
        val recentPriceTrend: Double?,
        val buildingCount: Int?,
        val averageBuildingAge: Double?
    )
    
    /**
     * 소셜 통계 정보 DTO
     */
    data class SocialInfo(
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
     * 인구통계 정보 DTO
     */
    data class DemographicInfo(
        val floatingPopulation: Int?,
        val populationDensityValue: Int?,
        val weekdayPeakHourPopulation: Int?,
        val weekendPeakHourPopulation: Int?,
        val ageDistribution: List<AgeDistribution>?,
        val genderRatio: List<GenderRatio>?,
        val visitorResidenceDistribution: List<VisitorResidence>?,
        val transportationUsage: List<TransportationUsage>?
    )
}