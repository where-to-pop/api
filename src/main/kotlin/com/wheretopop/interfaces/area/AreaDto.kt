package com.wheretopop.interfaces.area

import com.wheretopop.shared.model.statistics.AgeDistribution
import com.wheretopop.shared.model.statistics.BrandDistribution
import com.wheretopop.shared.model.statistics.GenderRatio
import com.wheretopop.shared.model.statistics.Hashtag
import com.wheretopop.shared.model.statistics.Keyword
import com.wheretopop.shared.model.statistics.StoreCategory
import com.wheretopop.shared.model.statistics.TransportationUsage
import com.wheretopop.shared.model.statistics.VisitorResidence
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Area 관련 인터페이스 레이어 DTO
 */
object AreaDto {

    /**
     * 지역 응답 DTO
     */
    @Schema(description = "지역 정보 응답")
    data class AreaResponse(
        @Schema(description = "지역 ID", example = "123456789")
        val id: Long,

        @Schema(description = "지역 이름", example = "강남역 일대")
        val name: String,

        @Schema(description = "지역 설명", example = "서울특별시 강남구 강남역 주변 상권")
        val description: String?,

        @Schema(description = "지역 위치 정보")
        val location: LocationResponse,

        @Schema(description = "상위 지역 ID", example = "10001")
        val regionId: Long?,

        @Schema(description = "지역 통계 정보 목록")
        val statistics: List<StatisticResponse>?
    )

    /**
     * 위치 정보 응답 DTO
     */
    @Schema(description = "위치 정보")
    data class LocationResponse(
        @Schema(description = "위도", example = "37.5013")
        val latitude: Double,

        @Schema(description = "경도", example = "127.0396")
        val longitude: Double
    )

    /**
     * 지역 통계 응답 DTO
     */
    @Schema(description = "지역 통계 정보")
    data class StatisticResponse(
        @Schema(description = "통계 ID", example = "987654321")
        val id: Long,

        @Schema(description = "통계 수집 시간", example = "2023-06-12T15:30:00")
        val collectedAt: LocalDateTime,

        @Schema(description = "상업 통계 정보")
        val commercial: CommercialResponse,

        @Schema(description = "부동산 통계 정보")
        val realEstate: RealEstateResponse,

        @Schema(description = "소셜 통계 정보")
        val social: SocialResponse,

        @Schema(description = "인구통계 정보")
        val demographic: DemographicResponse
    )

    /**
     * 상업 통계 응답 DTO
     */
    @Schema(description = "상업 통계 정보")
    data class CommercialResponse(
        @Schema(description = "상점 수", example = "1245")
        val storeCount: Int?,

        @Schema(description = "신규 상점 수", example = "52")
        val newStoreCount: Int?,

        @Schema(description = "폐업 상점 수", example = "23")
        val closedStoreCount: Int?,

        @Schema(description = "팝업 스토어 빈도", example = "15")
        val popupFrequencyCount: Int?,

        @Schema(description = "이벤트 수", example = "7")
        val eventCount: Int?,

        @Schema(description = "주요 상점 카테고리 목록")
        val mainStoreCategories: List<StoreCategory>?,

        @Schema(description = "브랜드 분포 목록")
        val brandDistribution: List<BrandDistribution>?
    )

    /**
     * 부동산 통계 응답 DTO
     */
    @Schema(description = "부동산 통계 정보")
    data class RealEstateResponse(
        @Schema(description = "평균 임대료", example = "3500000")
        val averageRent: Long?,

        @Schema(description = "평균 공실률", example = "12.5")
        val averageVacancyRate: Double?,

        @Schema(description = "최저 임대료", example = "1800000")
        val minRent: Long?,

        @Schema(description = "최고 임대료", example = "5200000")
        val maxRent: Long?,

        @Schema(description = "최근 가격 추세", example = "4.2")
        val recentPriceTrend: Double?,

        @Schema(description = "건물 수", example = "45")
        val buildingCount: Int?,

        @Schema(description = "평균 건물 연령", example = "12.7")
        val averageBuildingAge: Double?
    )

    /**
     * 소셜 통계 응답 DTO
     */
    @Schema(description = "소셜 통계 정보")
    data class SocialResponse(
        @Schema(description = "SNS 언급 횟수", example = "1240")
        val snsMentionCount: Int?,

        @Schema(description = "긍정 감성 비율", example = "65.3")
        val positiveSentimentRatio: Double?,

        @Schema(description = "부정 감성 비율", example = "15.5")
        val negativeSentimentRatio: Double?,

        @Schema(description = "중립 감성 비율", example = "19.2")
        val neutralSentimentRatio: Double?,

        @Schema(description = "상위 키워드 목록")
        val topKeywords: List<Keyword>?,

        @Schema(description = "상위 해시태그 목록")
        val topHashtags: List<Hashtag>?,

        @Schema(description = "인스타그램 게시물 수", example = "872")
        val instagramPostCount: Int?,

        @Schema(description = "블로그 게시물 수", example = "345")
        val blogPostCount: Int?,

        @Schema(description = "뉴스 기사 수", example = "28")
        val newsArticleCount: Int?
    )

    /**
     * 인구통계 응답 DTO
     */
    @Schema(description = "인구통계 정보")
    data class DemographicResponse(
        @Schema(description = "유동인구", example = "25000")
        val floatingPopulation: Int?,

        @Schema(description = "인구 밀집도 값", example = "820")
        val populationDensityValue: Int?,

        @Schema(description = "평일 최대 시간대 인구", example = "17500")
        val weekdayPeakHourPopulation: Int?,

        @Schema(description = "주말 최대 시간대 인구", example = "22300")
        val weekendPeakHourPopulation: Int?,

        @Schema(description = "연령대별 분포")
        val ageDistribution: List<AgeDistribution>?,

        @Schema(description = "성별 비율")
        val genderRatio: List<GenderRatio>?,

        @Schema(description = "방문자 거주지 분포")
        val visitorResidenceDistribution: List<VisitorResidence>?,

        @Schema(description = "교통수단 이용 비율")
        val transportationUsage: List<TransportationUsage>?
    )

    /**
     * 지역 검색 요청 DTO
     */
    @Schema(description = "지역 검색 요청")
    data class SearchRequest(
        @Schema(description = "검색 키워드", example = "강남")
        val keyword: String? = null,

        @Schema(description = "상위 지역 ID", example = "10001")
        val regionId: Long? = null,

        @Schema(description = "위도", example = "37.5013")
        val latitude: Double? = null,

        @Schema(description = "경도", example = "127.0396")
        val longitude: Double? = null,

        @Schema(description = "반경(km)", example = "2.0")
        val radius: Double? = null,

        @Schema(description = "최소 유동인구", example = "10000")
        val minFloatingPopulation: Int? = null,

        @Schema(description = "최소 상점 수", example = "500")
        val minStoreCount: Int? = null,

        @Schema(description = "최대 임대료", example = "4000000")
        val maxRent: Long? = null,

        @Schema(description = "결과 시작 오프셋", example = "0")
        val offset: Int = 0,

        @Schema(description = "결과 최대 개수", example = "20")
        val limit: Int = 20
    )
}
