package com.wheretopop.infrastructure.popup.external.popply

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import java.util.regex.Pattern

private val logger = KotlinLogging.logger {}

/**
 * Popply Popup 상세 페이지 크롤링 담당 클래스
 */
@Component
class PopupPopplyDetailCrawler(
    @Qualifier("popupApiWebClient") private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) {
    private val addressRegex: Pattern = Pattern.compile("^(.*?(?:로|길|가)\\s*\\d+)(?:\\s+(.*))?\$")

    /**
     * 주어진 popupId에 해당하는 팝업 상세 정보를 크롤링합니다.
     *
     * @param popupId 크롤링할 팝업의 ID
     * @return 크롤링된 PopupDetail 객체. 실패 시 null 반환.
     */
    suspend fun crawlDetail(popupId: Int): PopupDetail? {
        val relativePath = "/$popupId"
        logger.info { "Crawling popup detail for ID $popupId using relative path: $relativePath" }

        return try {
            val htmlResponse = webClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/{id}") // 경로 변수 템플릿
                        .build(popupId) // 템플릿에 실제 값 매핑
                }
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .onStatus({ status -> !status.is2xxSuccessful }) { response ->
                    logger.error { "Page loading failed for ID $popupId. Status code: ${response.statusCode()}" }
                    Mono.error(RuntimeException("Page loading failed with status code ${response.statusCode()}"))
                }
                .awaitBody<String>()

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

            val title = eventData.name?.trim() ?: ""
            val fullAddress = eventData.location?.firstOrNull()?.address?.name?.trim() ?: ""
            val startDate = eventData.startDate
            val endDate = eventData.endDate
            val description = eventData.description?.trim() ?: ""

            val matcher = addressRegex.matcher(fullAddress)
            val (address, optionalAddress) = if (matcher.find()) {
                val mainAddress = matcher.group(1)?.trim() ?: fullAddress
                val optAddress = matcher.group(2)?.trim() ?: ""
                mainAddress to optAddress
            } else {
                fullAddress to ""
            }

            val eventUrl = localBusinessData?.url
            val latitude = localBusinessData?.geo?.latitude
            val longitude = localBusinessData?.geo?.longitude
            val organizerName = eventData.organizer?.name
            val organizerUrl = eventData.organizer?.url

            logger.info { "[+] ID $popupId: Event information collected successfully." }

            PopupDetail(
                title = title,
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

}