package com.wheretopop.application.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface XUseCase {
    suspend fun crawlXAndSave(popup: Popup)
}