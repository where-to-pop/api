package com.wheretopop.application.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopplyUseCase {
    suspend fun crawlPopply(): List<PopupDetail>
    suspend fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
}