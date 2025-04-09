package com.wheretopop.shared.model.statistics

/**
 * 상점 카테고리 데이터 클래스
 * 각 상점 카테고리별 개수를 저장
 */
data class StoreCategory(
    val name: String,         // 카테고리 이름 (예: "카페", "의류", "식당")
    val count: Int,           // 해당 카테고리 매장 수
    val percentage: Double? = null  // 전체 매장 중 비율 (%)
)

/**
 * 브랜드 분포 데이터 클래스
 * 지역 내 브랜드 분포 정보를 저장
 */
data class BrandDistribution(
    val brandName: String,    // 브랜드 이름
    val count: Int,           // 해당 브랜드 매장 수
    val category: String? = null,    // 브랜드 카테고리 (예: "패션", "식품")
    val percentage: Double? = null  // 전체 브랜드 중 비율 (%)
) 