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

    override fun crawlPopplyAndSave() {
        popplyProcessor.crawlAndSave()
    }
    override fun crawlPopply():List<PopupDetail> {
        return popplyProcessor.crawl()
    }
    override fun savePopply(popupDetail: PopupDetail, popupId: PopupId) {
        popplyProcessor.save(popupDetail, popupId)
    }

    override fun embedAndSaveDetailedPopupInfo(popupInfo: PopupInfo.Detail) {
        popplyProcessor.saveEmbeddings(popupInfo)
    }
    override fun crawlXAndSave(popup: Popup) {
        xProcessor.crawlAndSaveByPopup(popup)
    }
}