package com.wheretopop.interfaces.popup

import com.wheretopop.application.popup.PopupFacade
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PopupScheduler(
    private val popupFacade: PopupFacade,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

//    @Scheduled(cron = "5 0 0 * * *")
    fun schedulePopupExternalDataIngestion() {
        logger.info("Start process popup retrieval")
        try {
            runBlocking {
//                val query = "홍대에서 핫한 팝업"
//                logger.info("query: $query")
//                val retrieval = popupFacade.findSimilarPopupInfos(query)
//                retrieval.forEach { d ->
//                    val popup = d.popup
//                    logger.info(popup.name, popup.areaName, d.score)
//                }

//                popupFacade.processPopupInfosForVectorSearch()
            }
            logger.info("Popup retrieval sync completed successfully")
        } catch (e: Exception) {
            logger.error("Error during popup retrieval synchronization", e)
        }
    }
}