package com.wheretopop.infrastructure.popup.external

import com.wheretopop.application.popup.PopplyUseCase
import com.wheretopop.application.popup.XUseCase
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import org.springframework.stereotype.Component

@Component
class XExternalManager(
    private val popupExternalStore: PopupExternalStore,
    private val popupExternalReader: PopupExternalReader
): XUseCase {
    override fun crawlXAndSave(popup: Popup) {
        popupExternalStore.crawlXAndSave(popup)
    }
}