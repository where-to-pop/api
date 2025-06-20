package com.wheretopop.infrastructure.building.register.external.vworld.areacode

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpMethod

private val logger = KotlinLogging.logger {}

/**
 * 주소를 지역 코드로 변환해주는 API를 담당하는 클래스
 * https://www.vworld.kr/dev/v4dv_search2_s001.do
 */
@Component
class AddressToAreaCodeApiCaller(
    @Qualifier("vWorldOpenApiRestTemplate") private val restTemplate: RestTemplate,
    @Value("\${openapi.v-world.key}") private val apiKey: String
) {
    fun fetchAreaCode(address: String): AreaCode? {
        return try {
            logger.info { "Fetching area code for address: $address" }
            
            val url = "/req/search?service=search&request=search&version=2.0&crs=EPSG:900913&size=10&page=1&type=address&category=road&format=json&errorformat=json&key={apiKey}&query={address}"
            
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                AddressToAreaCodeResponse::class.java,
                apiKey,
                address
            )
            
            val code = response.body?.response?.result?.items?.firstOrNull()?.id
                ?: throw IllegalStateException("결과가 없습니다.")

            AreaCode(
                sigunguCd = code.substring(0, 5),
                bjdongCd = code.substring(5, 10),
            )
        } catch (e: Exception) {
            logger.error(e) { "Error fetching area code retrieval for $address: ${e.message}" }
            null
        }
    }
}