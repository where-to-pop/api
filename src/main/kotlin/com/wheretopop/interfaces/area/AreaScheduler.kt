package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 권역(Area) 데이터 동기화를 위한 스케줄러
 * Spring WebFlux 환경을 고려하여 Mono 기반으로 작성
 */
@Component
class AreaSyncScheduler(
    private val areaFacade: AreaFacade
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 4 * * *")
    suspend fun scheduleAreaExternalDataIngestion() {
        logger.info("Starting area data synchronization")
        try {
            areaFacade.ingestAreaExternalData()
            logger.info("Area data synchronization completed successfully")
        } catch (e: Exception) {
            logger.error("Error during area data synchronization", e)
        }

    }
}
