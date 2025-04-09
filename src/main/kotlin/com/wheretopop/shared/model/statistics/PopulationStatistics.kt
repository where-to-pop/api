package com.wheretopop.shared.model.statistics

/**
 * 연령 분포 데이터 클래스
 * 특정 지역의 연령대별 인구 분포 정보
 */
data class AgeDistribution(
    val ageRange: String,     // 연령대 (예: "20-29", "30-39")
    val count: Int,           // 해당 연령대 인구 수
    val percentage: Double    // 전체 인구 중 비율 (%)
)

/**
 * 성별 분포 데이터 클래스
 * 특정 지역의 성별 인구 분포 정보
 */
data class GenderDistribution(
    val gender: String,       // 성별 ("남성" 또는 "여성")
    val count: Int,           // 해당 성별 인구 수
    val percentage: Double    // 전체 인구 중 비율 (%)
) 