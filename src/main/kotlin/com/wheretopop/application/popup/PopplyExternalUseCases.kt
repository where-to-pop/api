package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopplyUseCase {
    companion object {
        const val DEFAULT_K = 2
    }

    fun crawlPopply(): List<PopupDetail>
    fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    fun getPopplyList(): List<PopupInfo.Basic>
    fun embedAndSavePopupInfo(popupInfo: PopupInfo.Basic, areaId: Long, areaName: String, buildingId: Long, augmentedPopupInfo: PopupInfo.Augmented)
    fun getSimilarPopupInfos(query: String): List<PopupInfo.WithScore>
    fun isPopupInfoPersisted(id: Long): Boolean
    fun getPopupsByAreaId(areaId: Long, k: Int = DEFAULT_K): List<PopupInfo.WithScore>
    fun getPopupsByBuildingId(buildingId: Long, k: Int = DEFAULT_K): List<PopupInfo.WithScore>
    fun getPopupsByAreaName(areaName: String, k: Int = DEFAULT_K): List<PopupInfo.WithScore>
    fun getPopupsByTargetAgeGroup(ageGroup: String, query: String = "", k: Int = DEFAULT_K): List<PopupInfo.WithScore>
    fun getPopupsByCategory(category: String, k: Int = DEFAULT_K): List<PopupInfo.WithScore>
}