package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.domain.area.AreaId
import com.wheretopop.domain.area.AreaInfo
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.awaitFirstOrNull
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import java.time.ZoneId


internal class R2dbcAreaPopulationRepository(
    private val entityTemplate: R2dbcEntityTemplate,
) : AreaPopulationRepository {

    private val entityClass = AreaPopulationEntity::class.java

    override suspend fun save(entity: AreaPopulationEntity): AreaPopulationEntity {
        val existing = loadEntityById(entity.id)
        return if (existing == null) {
            entityTemplate.insert(entity).awaitSingle()
        } else {
            entityTemplate.update(entity).awaitSingle()
        }
    }

    override suspend fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity> =
        entities.map { save(it) }

    override suspend fun findLatestByAreaId(areaId: AreaId): AreaPopulationEntity? =
        entityTemplate
            .select(entityClass)
            .matching(
                query(
                    where("area_id").`is`(areaId)
                        .and("deleted_at").isNull
                ).sort(Sort.by(Sort.Direction.DESC, "population_update_time"))
            )
            .awaitFirstOrNull()

    /**
     * 지역 ID를 기반으로 인구 데이터 인사이트를 도출합니다.
     */
    override suspend fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight? {
        // 최신 인구 데이터 조회
        val latestData = findLatestByAreaId(areaId) ?: return null
        
        // 예측 데이터 파싱
        val forecastData = latestData.getForecastPopulations() ?: emptyList()
        
        // 피크 타임 정보 추출
        val peakTimes = extractPeakTimes(forecastData)
        
        // 주요 방문자 그룹 분석
        val mainVisitorGroup = determineMainVisitorGroup(latestData)
        
        // 지배적 연령대 확인
        val dominantAgeGroup = determineDominantAgeGroup(latestData)
        
        // 현재 추정 인구수 (최소값과 최대값의 평균)
        val currentPopulation = (latestData.populationMin + latestData.populationMax) / 2
        
        return AreaInfo.PopulationInsight(
            areaId = latestData.areaId,
            areaName = latestData.areaName,
            congestionLevel = latestData.congestionLevel,
            congestionMessage = latestData.congestionMessage,
            currentPopulation = currentPopulation,
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
                    dominantAgeGroup = dominantAgeGroup
                ),
                mainVisitorGroup = mainVisitorGroup
            ),
            peakTimes = peakTimes,
            lastUpdatedAt = latestData.populationUpdateTime
        )
    }
    
    /**
     * 여러 지역 ID 목록에 대한 인구 데이터 인사이트를 도출합니다.
     */
    override suspend fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight> {
        if (areaIds.isEmpty()) return emptyList()
        
        val result = mutableListOf<AreaInfo.PopulationInsight>()
        for (areaId in areaIds) {
            findPopulationInsightByAreaId(areaId)?.let { result.add(it) }
        }
        return result
    }
    
    /**
     * 예측 데이터에서 피크 타임 정보를 추출합니다.
     */
    private fun extractPeakTimes(forecastData: List<ForecastPopulation>): List<AreaInfo.PeakTimeInfo> {
        if (forecastData.isEmpty()) return emptyList()
        
        // 예측 데이터를 인구 수 기준으로 내림차순 정렬
        val sortedData = forecastData.sortedByDescending { it.forecastPopulationMax }
        
        // 상위 3개 시간대 선택 (또는 데이터가 3개 미만이면 모든 데이터)
        val topPeakHours = sortedData.take(3)
        
        return topPeakHours.map { forecast ->
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
    private fun determineCongestionLevel(population: Int): String {
        return when {
            population < 500 -> "여유"
            population < 1000 -> "보통"
            population < 2000 -> "혼잡"
            else -> "매우 혼잡"
        }
    }
    
    /**
     * 인구 밀집도 수준을 결정합니다.
     */
    private fun determineDensityLevel(data: AreaPopulationEntity): String {
        // 기존 혼잡도 레벨을 기반으로 밀집도 결정
        return when (data.congestionLevel) {
            "여유", "보통" -> "낮음"
            "약간 붐빔" -> "보통"
            "붐빔" -> "높음"
            "매우 붐빔" -> "매우 높음"
            else -> "보통"
        }
    }
    
    /**
     * 연령대와 성별 정보를 바탕으로 주요 방문자 그룹을 결정합니다.
     */
    private fun determineMainVisitorGroup(data: AreaPopulationEntity): String {
        // 주요 연령대 (가장 비율이 높은 연령대)
        val dominantAge = determineDominantAgeGroup(data)
        
        // 주요 성별 (비율이 더 높은 성별)
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

    private suspend fun loadEntityById(id: AreaPopulationId): AreaPopulationEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}

