package com.wheretopop.shared.model.statistics

import com.wheretopop.shared.enums.Gender

/**
 * 성별 비율 데이터 클래스
 * 방문자의 성별 분포 정보를 저장
 */
data class GenderRatio(
    val gender: Gender,       // 성별 (열거형: MALE, FEMALE)
    val count: Int,           // 해당 성별 방문자 수
    val percentage: Double    // 전체 방문자 중 비율 (%)
) 