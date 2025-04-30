package com.wheretopop.infrastructure.building.register.external.vworld.areacode

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

private val logger = KotlinLogging.logger {}

/**
 * 주소를 지역 코드로 변환해주는 API를 담당하는 클래스
 * https://www.vworld.kr/dev/v4dv_search2_s001.do
 */
@Component
class AddressToAreaCodeApiCaller(
    @Qualifier("vWorldOpenApiWebClient") private val webClient: WebClient,
    @Value("\${openapi.v-world.key}") private val apiKey: String
) {
    suspend fun fetchAreaCode(address: String): AreaCode? {
        return try {
            logger.info { "Fetching area code for address: $address" }
            val response = webClient.get()
                .uri { builder ->
                    builder
                        .path("/req/search")
                        .queryParam("service", "search")
                        .queryParam("request", "search")
                        .queryParam("version", "2.0")
                        .queryParam("crs", "EPSG:900913")
                        .queryParam("size", "10")
                        .queryParam("page", "1")
                        .queryParam("type", "address")
                        .queryParam("category", "road")
                        .queryParam("format", "json")
                        .queryParam("errorformat", "json")
                        .queryParam("key", apiKey)
                        .queryParam("query", address)
                        .build()
                }
                .retrieve()
                .awaitBody<AddressToAreaCodeResponse>()

            val code = response.response.result.items.firstOrNull()?.id
                ?: throw IllegalStateException("결과가 없습니다.")

            AreaCode(
                sigunguCd = code.substring(0, 5),
                bjdongCd = code.substring(5, 10),
                bun = code.substring(10, 14),
                ji = code.substring(14, 18)
            )
        } catch (e: Exception) {
            logger.error(e) { "Error fetching area code data for $address: ${e.message}" }
            null
        }
    }
}