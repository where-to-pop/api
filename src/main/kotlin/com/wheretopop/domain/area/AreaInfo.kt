package com.wheretopop.domain.area

import java.time.Instant

/**
 * 도메인 계층 외부에 넘겨줄 Area 관련 DTO 클래스들
 * 도메인 로직이 누설되지 않도록 데이터만 전달
 * AreaInfo는 data만 정의된 class로 사용, 변환의 책임은 mapper class를 활용
 */
class AreaInfo {

    /**
     * Area 기본 정보를 담은 DTO
     */
    data class Main(
        val id: AreaId,
        val name: String,
        val description: String?,
        val location: LocationInfo,
    )

    data class Detail(
        val id: AreaId,
        val name: String,
        val description: String?,
        val location: LocationInfo,
        val populationInsight: PopulationInsight?
    )
    
    /**
     * Location 정보를 담은 DTO
     */
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double
    )
    
    /**
     * 지역 인구 데이터 인사이트를 담은 DTO
     */
    data class PopulationInsight(
        val areaId: AreaId,
        val areaName: String,
        val congestionLevel: String,
        val congestionMessage: String,
        val currentPopulation: Int,              // 현재 추정 인구 수
        val populationDensity: PopulationDensity,
        val demographicInsight: DemographicInsight,
        val peakTimes: List<PeakTimeInfo>,       // 인구 피크 시간대 정보
        val lastUpdatedAt: Instant               // 데이터 최종 업데이트 시간
    )
    
    /**
     * 인구 밀집도 정보
     */
    data class PopulationDensity(
        val level: String,                       // 낮음, 보통, 높음, 매우 높음
        val residentRate: Double,                // 거주자 비율
        val nonResidentRate: Double              // 비거주자 비율 (외지인/관광객)
    )
    
    /**
     * 인구 통계학적 인사이트
     */
    data class DemographicInsight(
        val genderRatio: GenderRatio,
        val ageDistribution: AgeDistribution,
        val mainVisitorGroup: String             // 주요 방문자 그룹 (예: "20-30대 여성")
    )
    
    /**
     * 성별 비율
     */
    data class GenderRatio(
        val maleRate: Double,
        val femaleRate: Double
    )
    
    /**
     * 연령대별 분포
     */
    data class AgeDistribution(
        val under10Rate: Double,
        val age10sRate: Double,
        val age20sRate: Double,
        val age30sRate: Double,
        val age40sRate: Double,
        val age50sRate: Double,
        val age60sRate: Double,
        val over70sRate: Double,
        val dominantAgeGroup: String             // 가장 비율이 높은 연령대
    )
    
    /**
     * 인구 피크 시간 정보
     */
    data class PeakTimeInfo(
        val hour: Int,                           // 시간 (0-23)
        val expectedCongestion: String,          // 예상 혼잡도
        val populationEstimate: Int              // 예상 인구 수
    )
}