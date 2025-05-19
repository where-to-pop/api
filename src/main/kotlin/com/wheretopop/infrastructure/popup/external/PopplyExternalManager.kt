package com.wheretopop.infrastructure.popup.external

import com.wheretopop.application.popup.PopplyUseCase
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoMapper
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import org.springframework.stereotype.Component

@Component
class PopplyExternalManager(
    private val popupExternalStore: PopupExternalStore,
    private val popupExternalReader: PopupExternalReader
): PopplyUseCase {
    override fun crawlPopply():List<PopupDetail> {
        return popupExternalStore.crawlPopply()
    }
    override fun savePopply(popupDetail: PopupDetail, popupId: PopupId) {
        popupExternalStore.savePopply(popupDetail, popupId)
    }
    override fun getPopplyList(): List<PopupInfo.Basic> {
        return popupExternalReader.getAllPopply()
    }
    override fun embedAndSavePopupInfo(popupInfo: PopupInfo.Basic, areaId: Long, areaName: String, buildingId: Long) {
        val detailedPopupInfo = PopupInfoMapper.toDetail(popupInfo, areaId, areaName, buildingId)
        return popupExternalStore.embedAndSaveDetailedPopupInfo(detailedPopupInfo)
    }
    override fun getSimilarPopupInfos(query: String): List<PopupInfo.WithScore> {
        return popupExternalReader.getSimilarPopups(query)
    }

    override fun isPopupInfoPersisted(id: Long): Boolean {
        return popupExternalReader.isPopupInfoPersisted(id)
    }
}