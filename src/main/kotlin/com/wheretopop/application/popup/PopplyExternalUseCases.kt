package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopplyUseCase {
    fun crawlPopply(): List<PopupDetail>
    fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    fun getPopplyList(): List<PopupInfo.Basic>
    fun embedAndSavePopupInfo(popupInfo: PopupInfo.Basic, areaId: Long, areaName: String, buildingId: Long)
    fun getSimilarPopupInfos(query: String): List<PopupInfo.WithScore>
    fun isPopupInfoPersisted(id: Long): Boolean
}