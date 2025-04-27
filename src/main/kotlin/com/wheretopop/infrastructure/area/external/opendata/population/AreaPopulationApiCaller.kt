package com.wheretopop.infrastructure.area.external.opendata.population

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

private val logger = KotlinLogging.logger {}

/**
 * 서울시 실시간 인구데이터 API 호출을 담당하는 클래스
 */
@Component
class AreaPopulationApiCaller(
    private val webClient: WebClient,
    @Value("\${openapi.seoul.key}") private val apiKey: String
) {
    /**
     * 특정 지역의 인구 데이터를 가져옵니다.
     * 
     * @param areaName 조회할 지역 이름
     * @return API 응답 데이터
     */
    suspend fun fetchPopulationData(areaName: String): SeoulPopulationResponse? {
        return try {
            logger.info { "Fetching population data for area: $areaName" }
            webClient.get()
                .uri { builder ->
                    builder.path("/{key}/json/citydata_ppltn/1/5/{areaNm}")
                        .build(apiKey, areaName)
                }
                .retrieve()
                .awaitBody<SeoulPopulationResponse>()
        } catch (e: Exception) {
            logger.error(e) { "Error fetching population data for $areaName: ${e.message}" }
            null
        }
    }
}