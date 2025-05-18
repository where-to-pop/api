package com.wheretopop.infrastructure.popup.external

import com.wheretopop.application.popup.PopplyUseCase
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoWithScore
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import org.springframework.stereotype.Component

@Component
class PopplyExternalManager(
    private val popupExternalStore: PopupExternalStore,
    private val popupExternalReader: PopupExternalReader
): PopplyUseCase {
    override fun crawlPopply():List<PopupDetail> {
        return popupExternalStore.crawlPopply()
    }
    override fun savePopply(popupDetail: PopupDetail, popupId: PopupId) {
        popupExternalStore.savePopply(popupDetail, popupId)
    }
    override fun getPopplyList(): List<PopupInfo> {
        return popupExternalReader.getAllPopply()
    }
    override fun saveEmbeddedPopply(popupInfos: List<PopupInfo>) {
        return popupExternalStore.saveEmbeddedPopply(popupInfos)
    }
    override fun getSimilarPopupInfos(query: String): List<PopupInfoWithScore> {
        return popupExternalReader.getSimilarPopups(query)
    }
}