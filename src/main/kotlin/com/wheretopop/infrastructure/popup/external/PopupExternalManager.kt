package com.wheretopop.infrastructure.popup.external

import com.wheretopop.application.popup.PopupUseCase
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import org.springframework.stereotype.Component

@Component
class PopupExternalManager(
    private val popupExternalStore: PopupExternalStore,
    private val popupExternalReader: PopupExternalReader
): PopupUseCase {
    override suspend fun crawlPopplyAndSave() {
        return popupExternalStore.crawlPopplyAndSave()
    }
    override suspend fun crawlPopply():List<PopupDetail> {
        return popupExternalStore.crawlPopply()
    }
    override suspend fun savePopply(popupDetail: PopupDetail, popupId: PopupId) {
        popupExternalStore.savePopply(popupDetail, popupId)
    }
    override suspend fun crawlXAndSave(popup: Popup) {
        popupExternalStore.crawlXAndSave(popup)
    }
}