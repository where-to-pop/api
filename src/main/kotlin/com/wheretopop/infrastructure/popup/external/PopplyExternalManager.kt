package com.wheretopop.infrastructure.popup.external

import com.wheretopop.application.popup.PopplyUseCase
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import org.springframework.stereotype.Component

@Component
class PopplyExternalManager(
    private val popupExternalStore: PopupExternalStore,
    private val popupExternalReader: PopupExternalReader
): PopplyUseCase {
    override suspend fun crawlPopply():List<PopupDetail> {
        return popupExternalStore.crawlPopply()
    }
    override suspend fun savePopply(popupDetail: PopupDetail, popupId: PopupId) {
        popupExternalStore.savePopply(popupDetail, popupId)
    }
    override suspend fun getPopplyList(): List<PopupInfo> {
        return popupExternalReader.getAllPopply()
    }
    override suspend fun saveEmbeddedPopply(popupInfos: List<PopupInfo>) {
        return popupExternalStore.saveEmbeddedPopply(popupInfos)
    }
}