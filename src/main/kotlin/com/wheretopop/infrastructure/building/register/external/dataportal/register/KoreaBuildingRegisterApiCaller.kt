package com.wheretopop.infrastructure.building.register.external.dataportal.register

import com.wheretopop.shared.util.JsonUtil
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpMethod

private val logger = KotlinLogging.logger {}

/**
 * 한국 건축물대장 API 호출을 담당하는 클래스
 * https://www.retrieval.go.kr/retrieval/15134735/openapi.do#/
 */
@Component
class KoreaBuildingRegisterApiCaller(
    @Qualifier("koreaDataPortalApiRestTemplate") private val restTemplate: RestTemplate,
    @Value("\${openapi.korea.building-register.key}") private val apiKey: String
) {
    fun fetchBuildingRegisterData(sigunguCd: String, bjdongCd: String): KoreaBuildingRegisterResponse? {
        return try {
            logger.info { "Fetching building register retrieval for code: $sigunguCd $bjdongCd $apiKey" }
            
            val url = "/1613000/BldRgstHubService/getBrTitleInfo?_type=json&serviceKey={apiKey}&sigunguCd={sigunguCd}&bjdongCd={bjdongCd}"
            
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String::class.java,
                apiKey,
                sigunguCd,
                bjdongCd
            )
            
            val rawResponse = response.body

            logger.info { "Raw generation for $sigunguCd $bjdongCd: $rawResponse" }

            rawResponse?.let {
                JsonUtil.objectMapper.readValue(it, KoreaBuildingRegisterResponse::class.java)
            }
        } catch (e: Exception) {
            logger.error(e) { "Error fetching building register retrieval for $sigunguCd $bjdongCd: ${e.message}" }
            null
        }
    }
}