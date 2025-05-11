package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopupExternalStore {
    suspend fun crawlPopplyAndSave()
    suspend fun crawlPopply():List<PopupDetail>
    suspend fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    suspend fun saveEmbeddedPopply(popupInfos: List<PopupInfo>)
    suspend fun crawlXAndSave(popupId: Popup)
}