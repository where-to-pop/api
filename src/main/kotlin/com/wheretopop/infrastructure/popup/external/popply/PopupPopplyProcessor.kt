package com.wheretopop.infrastructure.popup.external.popply

import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

/**
 * 팝플리 팝업스토어 데이터 서비스
 * 크롤링과 데이터 저장의 전체 흐름을 관리합니다.
 */
@Service
class PopupPopplyProcessor(
    private val popupPopplyDetailCrawler: PopupPopplyDetailCrawler
) : PopplyProcessor {

    /**
     * for test
     */
    override suspend fun crawlAndSave() {
//        val popupDetailData = popupPopplyDetailCrawler.crawlDetail(15)
//        logger.info { "Popply Detail: ${popupDetailData}"}
    }

    /**
     * 기간에 맞춰 팝업 상세 정보들을 크롤링
     */
    suspend fun crawlPopupDetailsByPeriod(startDate: LocalDate, endDate: LocalDate) {}

    /**
     * 주어진 ID 범위에 해당하는 팝업 상세 정보들을 크롤링
     * for test
     */
    suspend fun crawlPopupDetailsByIdRange(startId: Int, endId: Int) {}
}