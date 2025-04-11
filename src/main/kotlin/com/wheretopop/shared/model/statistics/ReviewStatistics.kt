package com.wheretopop.shared.model.statistics

/**
 * 리뷰 정보 클래스
 * 장소나 건물에 대한 리뷰 정보를 담는 데이터 클래스
 */
data class Review(
    val id: String? = null,
    val content: String? = null,
    val rating: Double? = null,
    val sentiment: String? = null,
    val reviewDate: String? = null,
    val source: String? = null  // 리뷰 출처 (e.g. "Google", "Naver", "Kakao")
) 