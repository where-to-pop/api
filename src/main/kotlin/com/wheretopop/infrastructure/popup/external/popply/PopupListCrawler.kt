package com.wheretopop.infrastructure.popup.external.popply

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

private val logger = KotlinLogging.logger {}

@Component
class PopupListCrawler(
    private val driver: WebDriver,
    @Value("\${scraping.target.url:https://popply.co.kr/popup}") private val targetUrl: String,
    @Value("\${scraping.target.selector.firstPopupLink:div.popuplist-board > ul:nth-of-type(1) > li:nth-of-type(1) div.popup-info-wrap > a}") private val cssSelector: String,
    @Value("\${scraping.wait.timeoutSeconds:15}") private val timeoutSeconds: Long
) {

    suspend fun fetchFirstPopupId(): Int? = withContext(Dispatchers.IO) {
        try {
            driver.get(targetUrl)

            val wait = WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
            val firstListItemSelector = "div.popuplist-board > ul:nth-of-type(1) > li:nth-of-type(1)"
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(firstListItemSelector)))

            val linkElement: WebElement = driver.findElement(By.cssSelector(cssSelector))

            val hrefAttribute = linkElement.getAttribute("href")

            if (hrefAttribute == null) {
                logger.warn("href 속성을 찾을 수 없습니다.")
                return@withContext null
            }
            val idString = hrefAttribute.substringAfterLast('/', "")
            val popupId = idString.toIntOrNull()

            if (popupId == null) {
                logger.error("href ('{}')에서 마지막 숫자 ID를 추출하거나 Int로 변환할 수 없습니다. 추출된 문자열: '{}'", hrefAttribute, idString)
                return@withContext null
            }

            return@withContext popupId

        } catch (e: TimeoutException) {
            logger.error("페이지 로드 또는 요소 검색 시간 초과 ({}초): {}", timeoutSeconds, e.message)
            return@withContext null
        } catch (e: NoSuchElementException) {
            logger.error("지정한 CSS 선택자({})로 요소를 찾을 수 없습니다: {}", cssSelector, e.message)
            return@withContext null
        } catch (e: Exception) {
            logger.error("스크래핑 중 예상치 못한 오류 발생: {}", e.message, e)
            return@withContext null
        }
    }
}