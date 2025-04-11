package com.wheretopop.domain.area

import com.wheretopop.shared.model.UniqueId
import java.time.LocalDateTime
import com.wheretopop.domain.area.AreaStatistic
import com.wheretopop.shared.model.Location
/**
 * Area Aggregate Root
 * 특정 권역을 나타내는 애그리거트 루트 클래스
 */
class Area private constructor(
    val id: UniqueId,
    val name: String,
    val description: String?,
    val location: Location,
    val regionId: Long?,
    private val _statistics: MutableList<AreaStatistic> = mutableListOf()
) {
    // 통계 정보에 대한 읽기 전용 접근자
    val statistics: List<AreaStatistic>
        get() = _statistics.toList()

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            name: String,
            description: String? = null,
            location: Location,
            regionId: Long? = null,
        ): Area {
            require(name.isNotBlank()) { "지역 이름은 필수입니다." }
            return Area(
                id = id,
                name = name,
                description = description,
                location = location,
                regionId = regionId,
            )
        }
    }

    /**
     * 권역에 통계 정보 추가
     */
    fun addStatistic(statistic: AreaStatistic): AreaStatistic {
        val existingStat = _statistics.find { it.collectedAt == statistic.collectedAt }
        if (existingStat != null) {
            return existingStat
        }
        _statistics.add(statistic)
        return statistic
    }

    /**
     * 최신 통계 데이터 조회
     */
    fun getLatestStatistic(): AreaStatistic? {
        return _statistics.maxByOrNull { it.collectedAt }
    }

    /**
     * 특정 기간의 통계 데이터 조회
     */
    fun getStatisticsBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<AreaStatistic> {
        return _statistics.filter { it.collectedAt in startDate..endDate }
    }

    /**
     * 유동인구가 특정 임계치 이상인지 확인
     */
    fun isHighlyPopulated(threshold: Int): Boolean {
        val latestStat = getLatestStatistic() ?: return false
        return latestStat.demographic.floatingPopulation ?: 0 > threshold
    }

    /**
     * 상업적으로 활발한 지역인지 확인 (매장 밀도 기준)
     */
    fun isCommerciallyActive(storeCountThreshold: Int): Boolean {
        val latestStat = getLatestStatistic() ?: return false
        return latestStat.commercial.storeCount ?: 0 > storeCountThreshold
    }

    /**
     * 임대료 기준 비싼 지역인지 확인
     */
    fun isExpensiveArea(rentThreshold: Long): Boolean {
        val latestStat = getLatestStatistic() ?: return false
        return latestStat.realEstate.averageRent ?: 0 > rentThreshold
    }

    /**
     * SNS 인기 지역인지 확인 (언급 횟수 기준)
     */
    fun isPopularOnSocial(mentionThreshold: Int): Boolean {
        val latestStat = getLatestStatistic() ?: return false
        return latestStat.social.snsMentionCount ?: 0 > mentionThreshold
    }

    /**
     * 특정 연령대 방문이 많은지 확인
     */
    fun isPopularForAgeGroup(ageRange: String, percentage: Double): Boolean {
        val latestStat = getLatestStatistic() ?: return false
        val ageDistribution = latestStat.demographic.ageDistribution ?: return false
        val targetAge = ageDistribution.find { it.ageRange == ageRange }
        return targetAge?.percentage ?: 0.0 > percentage
    }
}
