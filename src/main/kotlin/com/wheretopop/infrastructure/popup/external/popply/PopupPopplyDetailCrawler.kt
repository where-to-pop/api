package com.wheretopop.infrastructure.popup.external.popply

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.wheretopop.infrastructure.area.external.opendata.population.StringToInstantDeserializer
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.regex.Pattern

private val logger = KotlinLogging.logger {}

/**
 * Popply Popup 상세 페이지 크롤링 담당 클래스
 */
@Component
class PopupPopplyDetailCrawler(
    @Qualifier("popupApiRestTemplate") private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    private val addressRegex: Pattern = Pattern.compile("^(.*?(?:로|길|가)\\s*\\d+)(?:\\s+(.*))?\$")

    /**
     * 주어진 popupId에 해당하는 팝업 상세 정보를 크롤링합니다.
     *
     * @param popupId 크롤링할 팝업의 ID
     * @return 크롤링된 PopupDetail 객체. 실패 시 null 반환.
     */
    fun crawlDetail(popupId: Int): PopupDetail? {
        val relativePath = "/$popupId"
        logger.info { "Crawling popup detail for ID $popupId using relative path: $relativePath" }

        return try {
            // RestTemplate은 기본적으로 상대 경로를 지원하지 않으므로 ID를 직접 URI에 추가
            val htmlResponse = try {
                restTemplate.getForObject("/{id}", String::class.java, popupId)
            } catch (e: HttpStatusCodeException) {
                logger.error { "Page loading failed for ID $popupId. Status code: ${e.statusCode}" }
                throw RuntimeException("Page loading failed with status code ${e.statusCode}")
            }

            if (htmlResponse == null) {
                logger.error { "Null response received for ID $popupId" }
                return null
            }

            val document = Jsoup.parse(htmlResponse)
            val jsonLdScriptElement = document.selectFirst("script[type=\"application/ld+json\"]")

            if (jsonLdScriptElement == null) {
                logger.warn { "[!] ID $popupId: JSON-LD script not found." }
                return null
            }

            val jsonLdString = jsonLdScriptElement.data()
            val jsonLdList: List<JsonLdData> = objectMapper.readValue(jsonLdString, object : TypeReference<List<JsonLdData>>() {})

            val eventData = jsonLdList.find { it.type == "Event" }
            val localBusinessData = jsonLdList.find { it.type == "LocalBusiness" }

            if (eventData == null) {
                logger.warn { "[!] ID $popupId: 'Event' type data not found in JSON-LD." }
                return null
            }

            val name = eventData.name?.trim() ?: ""
            val fullAddress = eventData.location?.firstOrNull()?.address?.name?.trim() ?: ""
            val startDate = toKorInstant(eventData.startDate)
            val endDate = toKorInstant(eventData.endDate)
            val description = eventData.description?.trim() ?: ""

            val matcher = addressRegex.matcher(fullAddress)
            val (address, optionalAddress) = if (matcher.find()) {
                val mainAddress = matcher.group(1)?.trim() ?: fullAddress
                val optAddress = matcher.group(2)?.trim() ?: ""
                mainAddress to optAddress
            } else {
                fullAddress to null
            }

            val eventUrl = localBusinessData?.url
            val latitude = localBusinessData?.geo?.latitude
            val longitude = localBusinessData?.geo?.longitude
            val organizerName = eventData.organizer?.name
            val organizerUrl = eventData.organizer?.url

            logger.info { "[+] ID $popupId: Event information collected successfully." }

            PopupDetail(
                name = name,
                address = address,
                optionalAddress = optionalAddress,
                startDate = startDate,
                endDate = endDate,
                description = description,
                url = eventUrl,
                latitude = latitude,
                longitude = longitude,
                organizerName = organizerName,
                organizerUrl = organizerUrl,
                popplyId = popupId
            )

        } catch(e: Exception) {
            logger.error(e) { "[!] ID $popupId: Failed to crawl or process data: ${e.message}" }
            null
        }
    }

    /**
     * "2023-02-24T00:00:00" 같은 문자열(KST 기준)을 받아서 Instant로 변환
     */
    fun toKorInstant(timeString: String?): Instant? {
        if (timeString == null) return null
        val localDateTime = LocalDateTime.parse(timeString)
        val seoulZone = ZoneId.of("Asia/Seoul")
        return localDateTime.atZone(seoulZone).toInstant()
    }
}