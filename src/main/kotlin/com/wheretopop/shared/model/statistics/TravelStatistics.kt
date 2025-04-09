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
    val transportType: String,  // 교통수단 (예: "지하철", "버스", "자가용")
    val count: Int,             // 해당 교통수단 이용자 수
    val percentage: Double,     // 전체 방문자 중 비율 (%)
    val averageTime: Int? = null  // 평균 이동 시간 (분)
) 