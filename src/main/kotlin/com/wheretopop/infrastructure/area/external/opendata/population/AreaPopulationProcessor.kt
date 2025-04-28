package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.infrastructure.area.AreaRepository
import com.wheretopop.infrastructure.area.external.opendata.OpenDataProcessor
import com.wheretopop.infrastructure.area.external.opendata.OpenDataType
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * 서울시 실시간 인구데이터 서비스
 * API 호출과 데이터 저장의 전체 흐름을 관리합니다.
 */
@Service
class AreaPopulationProcessor(
    private val areaPopulationApiCaller: AreaPopulationApiCaller,
    private val areaPopulationRepository: AreaPopulationRepository,
    private val areaRepository: AreaRepository,
) : OpenDataProcessor {

    override fun support(type: OpenDataType): Boolean {
        return type == OpenDataType.AREA_POPULATION
    }

    /**
     * 모든 지역의 인구 데이터를 수집하고 저장합니다.
     */
    override suspend fun callAndSave() {
        val areas = areaRepository.findAll()
        logger.info { "Starting population data collection for ${areas.size} areas" }
        
        var successCount = 0
        var failCount = 0
        
        for (area in areas) {
            try {
                // API 호출
                val response = areaPopulationApiCaller.fetchPopulationData(area.name)
                if (response != null) {
                    // 응답 데이터 변환 및 저장
                    response.cityDataPopulation.forEach { cityDataPopulation ->
                        val areaPopulationEntity = AreaPopulationEntity.of(cityDataPopulation, area.id)
                        areaPopulationRepository.save(areaPopulationEntity)
                    }
                    
                    successCount++
                    logger.info { "Population data saved for area: ${area.name}" }
                } else {
                    failCount++
                    logger.warn { "Failed to fetch population data for area: ${area.name}" }
                }
            } catch (e: Exception) {
                failCount++
                logger.error(e) { "Error processing population data for ${area.name}: ${e.message}" }
            }
        }
        
        logger.info { "Population data collection completed. Success: $successCount, Fail: $failCount" }
    }
}
