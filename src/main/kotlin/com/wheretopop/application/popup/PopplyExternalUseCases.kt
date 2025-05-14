package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopplyUseCase {
    suspend fun crawlPopply(): List<PopupDetail>
    suspend fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    suspend fun getPopplyList(): List<PopupInfo>
    suspend fun saveEmbeddedPopply(popupInfos: List<PopupInfo>)
    suspend fun getSimilarPopupInfos(query: String)
}