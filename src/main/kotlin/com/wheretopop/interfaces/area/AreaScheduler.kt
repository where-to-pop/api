package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

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
    fun callOpenDataApi(): Mono<Unit> {
        TODO("공공 데이터 API 호출 부분 구현")
    }
}
