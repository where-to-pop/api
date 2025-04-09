package com.wheretopop.shared.model.statistics

/**
 * 키워드 데이터 클래스
 * SNS 데이터 분석에서 추출한 주요 키워드 정보
 */
data class Keyword(
    val word: String,         // 키워드 텍스트
    val frequency: Int,       // 출현 빈도
    val sentimentScore: Double? = null,  // 감성 점수 (-1.0 ~ 1.0)
    val category: String? = null        // 키워드 카테고리 (선택적)
)

/**
 * 해시태그 데이터 클래스
 * SNS 데이터 분석에서 추출한 주요 해시태그 정보
 */
data class Hashtag(
    val tag: String,         // 해시태그 (# 제외)
    val frequency: Int,      // 출현 빈도
    val popularityScore: Double? = null // 인기도 점수 (0.0 ~ 1.0)
) 