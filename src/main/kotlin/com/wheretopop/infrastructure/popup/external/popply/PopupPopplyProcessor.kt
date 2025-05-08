package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.infrastructure.popup.PopupRepository
import mu.KotlinLogging
import org.bouncycastle.asn1.cmc.CMCStatus.success
import org.springframework.stereotype.Service
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

/**
 * 팝플리 팝업스토어 데이터 서비스
 * 크롤링과 데이터 저장의 전체 흐름을 관리합니다.
 */
@Service
class PopupPopplyProcessor(
    private val popupPopplyDetailCrawler: PopupPopplyDetailCrawler,
    private val popupPopplyRepository: PopupPopplyRepository,
    private val popupRepository: PopupRepository,
    private val popupListCrawler: PopupListCrawler,
) : PopplyProcessor {

    override suspend fun crawlAndSave() {
        val popupId = popupListCrawler.fetchFirstPopupId()
        if (popupId == null) return
        crawlDownPopupDetailsByIdAndSave(popupId)
    }

    override suspend fun crawl():List<PopupDetail> {
        val popupId = popupListCrawler.fetchFirstPopupId()
        if (popupId == null) return emptyList()
        return crawlDownPopupDetailsById(popupId)
    }

    override suspend fun save(popupDetail: PopupDetail, popupId: PopupId) {
        val popupPopplyEntity = PopupPopplyEntity.of(
            popupDetail = popupDetail,
            popupId = popupId.toLong(),
        )
        popupPopplyRepository.save(popupPopplyEntity)
    }

    suspend fun getPopupDetail(popplyId: Int): PopupDetail? {
        val existing = popupPopplyRepository.findByPopplyId(popplyId)
        if (existing != null) return null

        val popupDetailData: PopupDetail = popupPopplyDetailCrawler.crawlDetail(popplyId)
            ?: run {
                logger.error("ID {} 에 해당하는 Popply 상세 정보를 찾을 수 없습니다.", popplyId)
                return null
            }

        return popupDetailData
    }

    suspend fun crawlDownPopupDetailsById(endId: Int, startId:Int = 15): List<PopupDetail> {
        logger.info("ID 범위 {}부터 {}까지 (역순) 팝업 상세 정보 크롤링 시작...", startId, endId)
        var successCount = 0
        var stoppedEarly = false
        var processedCount = 0

        val popupDetailList = mutableListOf<PopupDetail>()

        for (id in endId downTo startId) {
            processedCount++
            logger.debug("ID {} 처리 시도", id)

            val popupDetail = getPopupDetail(id)

            if (popupDetail !== null) {
                popupDetailList.add(popupDetail)
                successCount++
            } else {
                logger.info("ID {} 처리 중 중단 조건 발견. 크롤링을 중단합니다.", id)
                stoppedEarly = true
                break
            }
        }

        logger.info(
            "Popups Crawling Completed: Attempts {}, Success {}, {}",
            processedCount, successCount, if (stoppedEarly) "Stopped" else "Normally Ended"
        )

        return popupDetailList
    }

    /**
     * 팝플리에서 하나의 팝업 정보를 crawl
     * Popup 생성
     * popup_id 넘겨줌
     * 도메인 객체로 변환 (Popup)
     * 도메인 객체 저장
     */
    suspend fun processAndSavePopupDetail(popplyId: Int): Boolean {
        val existing = popupPopplyRepository.findByPopplyId(popplyId)
        if (existing != null) return false
//        logger.info("Popply ID {} 상세 정보 처리 시작...", popplyId)
        val popupDetailData: PopupDetail = popupPopplyDetailCrawler.crawlDetail(popplyId)
            ?: run {
                logger.error("ID {} 에 해당하는 Popply 상세 정보를 찾을 수 없습니다.", popplyId)
                return false
            }

        val newPopup = Popup.create(
            name = popupDetailData.name,
            address = popupDetailData.address,
        )
//        logger.info("새로운 Popup 객체 생성 완료: 이름={}", newPopup.name)

        val savedPopup = popupRepository.save(newPopup)

        val generatedPopupId = savedPopup.id
//        logger.info("PopupEntity 저장 완료: ID={}", generatedPopupId.value)

        val popupPopplyEntity = PopupPopplyEntity.of(
            popupDetail = popupDetailData,
            popupId = generatedPopupId.toLong(),
        )

        popupPopplyRepository.save(popupPopplyEntity)
//        logger.info("PopupPopplyEntity 저장 완료.")
        return true
    }

    /**
     * 기간에 맞춰 팝업 상세 정보들을 크롤링
     */
    suspend fun crawlPopupDetailsByPeriod(startDate: LocalDate, endDate: LocalDate) {}

    /**
     * 주어진 ID부터 역순으로 팝업 상세 정보들을 크롤링
     * 이미 있는 Popup 이면, 종료!
     */
    suspend fun crawlDownPopupDetailsByIdAndSave(endId: Int, startId:Int = 15) {
        logger.info("ID 범위 {}부터 {}까지 (역순) 팝업 상세 정보 크롤링 시작...", startId, endId)
        var successCount = 0
        var stoppedEarly = false
        var processedCount = 0

        for (id in endId downTo startId) {
            processedCount++
            logger.debug("ID {} 처리 시도", id)

            val success = processAndSavePopupDetail(id)

            if (success) {
                successCount++
            } else {
                logger.info("ID {} 처리 중 중단 조건 발견. 크롤링을 중단합니다.", id)
                stoppedEarly = true
                break
            }
        }

        logger.info(
            "Popups Crawling Completed: Attempts {}, Success {}, {}",
            processedCount, successCount, if (stoppedEarly) "Stopped" else "Normally Ended"
        )
    }
}