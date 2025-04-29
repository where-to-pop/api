package com.wheretopop.infrastructure.building.register.external.korea_building_register

import com.wheretopop.shared.util.JsonUtil
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

private val logger = KotlinLogging.logger {}

/**
 * 한국 건축물대장 API 호출을 담당하는 클래스
 * https://www.data.go.kr/data/15134735/openapi.do#/
 */
@Component
class KoreaBuildingRegisterApiCaller(
    @Qualifier("koreaDataPortalApiWebClient") private val webClient: WebClient,
    @Value("\${openapi.korea.building-register.key}") private val apiKey: String
) {
    suspend fun fetchBuildingRegisterData(sigunguCd: String, bjdongCd: String, bun: String, ji: String): KoreaBuildingRegisterResponse? {
        return try {
            logger.info { "Fetching building register data for code: $sigunguCd $bjdongCd $bun $ji" }
            val rawResponse = webClient.get()
                .uri { builder ->
                    builder
                        .path("/1613000/BldRgstHubService/getBrTitleInfo")
                        .queryParam("serviceKey", apiKey)
                        .queryParam("sigunguCd", sigunguCd)
                        .queryParam("bjdongCd", bjdongCd)
                        .queryParam("bun", bun)
                        .queryParam("ji", ji)
                        .build()
                }
                .retrieve()
                .awaitBody<String>()

            logger.info { "Raw response for $sigunguCd $bjdongCd $bun $ji: $rawResponse" } // 찍는다

            JsonUtil.objectMapper.readValue(rawResponse, KoreaBuildingRegisterResponse::class.java)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching builing register data for $sigunguCd $bjdongCd $bun $ji: ${e.message}" }
            null
        }
    }
}