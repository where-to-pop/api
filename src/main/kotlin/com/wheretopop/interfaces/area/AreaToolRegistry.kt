package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.wheretopop.domain.area.AreaId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.reactor.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import javax.annotation.PreDestroy

/**
 * 지역 정보 검색을 위한 AI 도구 레지스트리입니다.
 * Spring AI의 Tool Calling 기능을 활용하여 지역 데이터를 JSON 형태로 제공합니다.
 */
@Component
class AreaToolRegistry(
    private val areaFacade: AreaFacade,
    private val toolDispatcher: kotlinx.coroutines.CoroutineDispatcher
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 모든 지역 정보를 조회합니다.
     * AI 모델이 사용자에게 전체 지역 목록이 필요할 때 이 도구를 호출합니다.
     * 
     * @return 모든 지역 정보가 포함된 JSON 문자열
     */
    @Tool(description = "모든 지역 정보를 조회합니다. 사용자가 전체 지역 목록이나 지역 데이터를 요청할 때 사용하세요.")
    fun findAllArea(): String {
        logger.info("findAllArea 도구가 호출되었습니다")
        
        // 별도의 쓰레드 풀에서 suspend 함수 호출
        val areas = runBlocking(toolDispatcher) {
            logger.info("별도 쓰레드에서 코루틴 실행 중 - findAllArea")
            areaFacade.findAll()
        }
        
        logger.info("총 ${areas.size}개 지역 정보를 조회했습니다")
        
        return """
        {
            "status": "success",
            "count": ${areas.size},
            "areas": [
                ${areas.joinToString(",\n") { area ->
                    """
                    {
                        "id": "${area.id}",
                        "name": "${area.name}",
                        "description": "${area.description?.replace("\"", "\\\"") ?: ""}",
                        "location": {
                            "latitude": ${area.location.latitude},
                            "longitude": ${area.location.longitude}
                        }
                    }
                    """.trimIndent()
                }}
            ]
        }
        """.trimIndent()
    }

    /**
     * 특정 ID에 해당하는 지역의 상세 정보를 조회합니다.
     * AI 모델이 특정 지역에 대한 상세 정보가 필요할 때 이 도구를 호출합니다.
     * 
     * @param id 조회할 지역의 ID
     * @return 해당 지역의 상세 정보가 포함된 JSON 문자열
     */
    @Tool(description = "ID로 특정 지역의 상세 정보를 조회합니다. 사용자가 특정 지역의 상세 정보, 인구 통계, 혼잡도 등을 요청할 때 사용하세요.")
    fun findAreaById(id: String): String {
        logger.info("findAreaById 도구가 호출되었습니다: id={}", id)
        
        // 별도의 쓰레드 풀에서 suspend 함수 호출
        val area = runBlocking(toolDispatcher) {
            logger.info("별도 쓰레드에서 코루틴 실행 중 - findAreaById")
            areaFacade.getAreaDetailById(AreaId.of(id))
        }
        
        if (area == null) {
            logger.warn("ID가 {}인 지역을 찾을 수 없습니다", id)
            return """
            {
                "status": "error",
                "message": "해당 ID를 가진 지역을 찾을 수 없습니다: $id"
            }
            """.trimIndent()
        }
        
        logger.info("ID {}에 해당하는 지역 정보를 성공적으로 조회했습니다: {}", id, area.name)
        
        return """
        {
            "status": "success",
            "area": {
                "id": "${area.id}",
                "name": "${area.name}",
                "description": "${area.description?.replace("\"", "\\\"") ?: ""}",
                "location": {
                    "latitude": ${area.location.latitude},
                    "longitude": ${area.location.longitude}
                },
                "populationInsight": {
                    "areaId": "${area.populationInsight?.areaId ?: ""}",
                    "areaName": "${area.populationInsight?.areaName ?: ""}",
                    "congestionLevel": "${area.populationInsight?.congestionLevel ?: ""}",
                    "congestionMessage": "${area.populationInsight?.congestionMessage?.replace("\"", "\\\"") ?: ""}",
                    "currentPopulation": ${area.populationInsight?.currentPopulation ?: 0},
                    "populationDensity": {
                        "level": "${area.populationInsight?.populationDensity?.level ?: ""}",
                        "residentRate": ${area.populationInsight?.populationDensity?.residentRate ?: 0.0},
                        "nonResidentRate": ${area.populationInsight?.populationDensity?.nonResidentRate ?: 0.0}
                    },
                    "lastUpdatedAt": "${area.populationInsight?.lastUpdatedAt ?: ""}"
                }
            }
        }
        """.trimIndent()
    }

    /**
     * 위치(좌표) 기반으로 가장 가까운 지역을 찾습니다.
     * AI 모델이 사용자의 현재 위치에 가까운 지역을 찾을 때 사용합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @return 가장 가까운 지역 정보가 포함된 JSON 문자열
     */
    @Tool(description = "위도와 경도를 기반으로 가장 가까운 지역을 찾습니다. 사용자가 현재 위치 주변 지역이나 특정 좌표 근처의 지역을 알고 싶을 때 사용하세요.")
    fun findNearestArea(latitude: Double, longitude: Double): String {
        logger.info("findNearestArea 도구가 호출되었습니다: latitude={}, longitude={}", latitude, longitude)
        
        // 실제 구현은 areaFacade에 추가 기능이 필요합니다.
        // 아래는 예시 응답 형식입니다.
        return """
        {
            "status": "success",
            "message": "가장 가까운 지역을 찾았습니다",
            "area": {
                "id": "sample-id",
                "name": "샘플 지역",
                "description": "이 기능은 아직 구현되지 않았습니다",
                "distance": "0.5km",
                "location": {
                    "latitude": $latitude,
                    "longitude": $longitude
                }
            }
        }
        """.trimIndent()
    }
}

/**
 * Spring AI Tool 호출을 위한 코루틴 디스패처 설정
 * WebFlux 환경에서 안전하게 suspend 함수를 호출하기 위한 별도의 쓰레드 풀을 제공합니다.
 */
@Configuration
class ToolDispatcherConfig {
    private lateinit var scheduler: Scheduler
    private lateinit var dispatcher: CoroutineDispatcher

    @Bean
    fun toolDispatcher(): CoroutineDispatcher {
        scheduler = Schedulers.boundedElastic()
        dispatcher = scheduler.asCoroutineDispatcher()
        return dispatcher
    }

    @PreDestroy
    fun cleanup() {
        scheduler.dispose() // 🔥 중요: Reactor Scheduler는 close가 아니라 dispose()!
        // dispatcher는 따로 close 불필요
    }
}
