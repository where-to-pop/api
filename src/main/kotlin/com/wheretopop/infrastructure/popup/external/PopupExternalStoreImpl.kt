package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopplyProcessor
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import com.wheretopop.infrastructure.popup.external.x.XProcessor
import org.springframework.stereotype.Component

@Component
class PopupExternalStoreImpl(
    private val popplyProcessor: PopplyProcessor,
    private val xProcessor: XProcessor,
): PopupExternalStore {

    override suspend fun crawlPopplyAndSave() {
        popplyProcessor.crawlAndSave()
    }
    override suspend fun crawlPopply():List<PopupDetail> {
        return popplyProcessor.crawl()
    }
    override suspend fun savePopply(popupDetail: PopupDetail, popupId: PopupId) {
        popplyProcessor.save(popupDetail, popupId)
    }

    override suspend fun saveEmbeddedPopply(popupInfos: List<PopupInfo>) {
        popplyProcessor.saveEmbeddings(popupInfos)
    }
    override suspend fun crawlXAndSave(popup: Popup) {
        xProcessor.crawlAndSaveByPopup(popup)
    }
}