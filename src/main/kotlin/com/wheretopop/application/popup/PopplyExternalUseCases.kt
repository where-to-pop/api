package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoWithScore
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopplyUseCase {
    fun crawlPopply(): List<PopupDetail>
    fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    fun getPopplyList(): List<PopupInfo>
    fun saveEmbeddedPopply(popupInfos: List<PopupInfo>)
    fun getSimilarPopupInfos(query: String): List<PopupInfoWithScore>
}