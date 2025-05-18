package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.shared.infrastructure.entity.AreaPopulationEntity
import com.wheretopop.shared.domain.identifier.AreaPopulationId
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.time.ZoneId

/**
 * 지역 인구 데이터 리포지토리 JPA 구현체
 */
@Repository
@Transactional
class JpaAreaPopulationRepository(
    @PersistenceContext private val entityManager: EntityManager
) : AreaPopulationRepository {

    override fun save(entity: AreaPopulationEntity): AreaPopulationEntity {
        entityManager.persist(entity)
        return entity
    }

    override fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity> =
        entities.map { save(it) }

    override fun findLatestByAreaId(areaId: AreaId): AreaPopulationEntity? {
        val query = entityManager.createQuery(
            """
            SELECT a FROM AreaPopulationEntity a 
            WHERE a.areaId = :areaId 
            AND a.deletedAt IS NULL 
            ORDER BY a.populationUpdateTime DESC
            """,
            AreaPopulationEntity::class.java
        )
        query.setParameter("areaId", areaId)
        query.maxResults = 1
        
        return query.resultList.firstOrNull()
    }

    /**
     * 지역 ID를 기반으로 인구 데이터 인사이트를 도출합니다.
     */
    override fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight? {
        val latestData = findLatestByAreaId(areaId) ?: return null
        val forecastData = latestData.getForecastPopulations() ?: emptyList()
        
        return AreaInfo.PopulationInsight(
            areaId = latestData.areaId,
            areaName = latestData.areaName,
            congestionLevel = latestData.congestionLevel,
            congestionMessage = latestData.congestionMessage,
            currentPopulation = (latestData.populationMin + latestData.populationMax) / 2,
            populationDensity = AreaInfo.PopulationDensity(
                level = determineDensityLevel(latestData),
                residentRate = latestData.residentPopulationRate,
                nonResidentRate = latestData.nonResidentPopulationRate
            ),
            demographicInsight = AreaInfo.DemographicInsight(
                genderRatio = AreaInfo.GenderRatio(
                    maleRate = latestData.malePopulationRate,
                    femaleRate = latestData.femalePopulationRate
                ),
                ageDistribution = AreaInfo.AgeDistribution(
                    under10Rate = latestData.populationRate0,
                    age10sRate = latestData.populationRate10,
                    age20sRate = latestData.populationRate20,
                    age30sRate = latestData.populationRate30,
                    age40sRate = latestData.populationRate40,
                    age50sRate = latestData.populationRate50,
                    age60sRate = latestData.populationRate60,
                    over70sRate = latestData.populationRate70,
                    dominantAgeGroup = determineDominantAgeGroup(latestData)
                ),
                mainVisitorGroup = determineMainVisitorGroup(latestData)
            ),
            peakTimes = extractPeakTimes(forecastData),
            lastUpdatedAt = latestData.populationUpdateTime
        )
    }
    
    /**
     * 여러 지역 ID 목록에 대한 인구 데이터 인사이트를 도출합니다.
     */
    override fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight> =
        areaIds.mapNotNull { findPopulationInsightByAreaId(it) }
    
    /**
     * 예측 데이터에서 피크 타임 정보를 추출합니다.
     */
    private fun extractPeakTimes(forecastData: List<ForecastPopulation>): List<AreaInfo.PeakTimeInfo> {
        if (forecastData.isEmpty()) return emptyList()
        
        return forecastData
            .sortedByDescending { it.forecastPopulationMax }
            .take(3)
            .map { forecast ->
                val hour = forecast.forecastTime.atZone(ZoneId.systemDefault()).hour
                AreaInfo.PeakTimeInfo(
                    hour = hour,
                    expectedCongestion = determineCongestionLevel(forecast.forecastPopulationMax),
                    populationEstimate = (forecast.forecastPopulationMin + forecast.forecastPopulationMax) / 2
                )
            }
    }
    
    /**
     * 인구 수를 기반으로 혼잡도 수준을 결정합니다.
     */
    private fun determineCongestionLevel(population: Int): String = when {
        population < 500 -> "여유"
        population < 1000 -> "보통"
        population < 2000 -> "혼잡"
        else -> "매우 혼잡"
    }
    
    /**
     * 인구 밀집도 수준을 결정합니다.
     */
    private fun determineDensityLevel(data: AreaPopulationEntity): String = when (data.congestionLevel) {
        "여유", "보통" -> "낮음"
        "약간 붐빔" -> "보통"
        "붐빔" -> "높음"
        "매우 붐빔" -> "매우 높음"
        else -> "보통"
    }
    
    /**
     * 연령대와 성별 정보를 바탕으로 주요 방문자 그룹을 결정합니다.
     */
    private fun determineMainVisitorGroup(data: AreaPopulationEntity): String {
        val dominantAge = determineDominantAgeGroup(data)
        val dominantGender = if (data.malePopulationRate > data.femalePopulationRate) "남성" else "여성"
        return "$dominantAge $dominantGender"
    }
    
    /**
     * 가장 비율이 높은 연령대를 결정합니다.
     */
    private fun determineDominantAgeGroup(data: AreaPopulationEntity): String {
        val ageRates = mapOf(
            "10대 미만" to data.populationRate0,
            "10대" to data.populationRate10,
            "20대" to data.populationRate20,
            "30대" to data.populationRate30,
            "40대" to data.populationRate40,
            "50대" to data.populationRate50,
            "60대" to data.populationRate60,
            "70대 이상" to data.populationRate70
        )
        
        return ageRates.maxByOrNull { it.value }?.key ?: "알 수 없음"
    }
}

