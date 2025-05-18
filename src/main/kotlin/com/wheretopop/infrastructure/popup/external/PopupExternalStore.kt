package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopupExternalStore {
    fun crawlPopplyAndSave()
    fun crawlPopply():List<PopupDetail>
    fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    fun saveEmbeddedPopply(popupInfos: List<PopupInfo>)
    fun crawlXAndSave(popupId: Popup)
}