package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.shared.util.JsonUtil.objectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpMethod

private val logger = KotlinLogging.logger {}

/**
 * 서울시 실시간 인구데이터 API 호출을 담당하는 클래스
 * JPA 기반으로 변경됨
 */
@Component
class AreaPopulationApiCaller(
    @Qualifier("seoulApiRestTemplate") private val restTemplate: RestTemplate,
    @Value("\${openapi.seoul.key}") private val apiKey: String
) {
    /**
     * 특정 지역의 인구 데이터를 가져옵니다.
     * 
     * @param areaName 조회할 지역 이름
     * @return API 응답 데이터
     */
    fun fetchPopulationData(areaName: String): SeoulPopulationResponse? {
        return try {
            logger.info { "Fetching population data for area: $areaName" }
            
            val url = "/{key}/json/citydata_ppltn/1/5/{areaNm}"
                .replace("{key}", apiKey)
                .replace("{areaNm}", areaName)
            
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String::class.java
            )
            
            val rawResponse = response.body

            logger.info { "Raw response for $areaName: $rawResponse" } // 찍는다

            rawResponse?.let {
                objectMapper.readValue(it, SeoulPopulationResponse::class.java)
            }
        } catch (e: Exception) {
            logger.error(e) { "Error fetching population data for $areaName: ${e.message}" }
            null
        }
    }
}