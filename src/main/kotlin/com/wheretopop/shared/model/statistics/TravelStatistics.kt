package com.wheretopop.shared.model.statistics

/**
 * 방문자 거주지 분포 데이터 클래스
 * 방문자의 거주 지역별 분포 정보를 저장
 */
data class VisitorResidence(
    val region: String,       // 지역명 (예: "서울", "경기")
    val count: Int,           // 해당 지역 방문자 수
    val percentage: Double,   // 전체 방문자 중 비율 (%)
    val district: String? = null  // 세부 지역 (예: "강남구", "마포구")
)

/**
 * 교통수단 이용 비율 데이터 클래스
 * 방문자가 이용한 교통수단별 비율 정보를 저장
 */
data class TransportationUsage(
    val subwayUsage: Double? = null,    // 지하철 이용 비율 (%)
    val busUsage: Double? = null,       // 버스 이용 비율 (%)
    val taxiUsage: Double? = null,      // 택시 이용 비율 (%)
    val personalVehicleUsage: Double? = null, // 개인차량 이용 비율 (%)
    val walkingUsage: Double? = null,   // 도보 이용 비율 (%)
    val peakHours: List<Int>? = null    // 교통 이용 피크 시간대 (0-23)
) 