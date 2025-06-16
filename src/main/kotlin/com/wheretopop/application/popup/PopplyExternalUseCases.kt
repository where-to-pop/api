package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail

interface PopplyUseCase {
    fun crawlPopply(): List<PopupDetail>
    fun savePopply(popupDetail: PopupDetail, popupId: PopupId)
    fun getPopplyList(): List<PopupInfo.Basic>
    fun embedAndSavePopupInfo(popupInfo: PopupInfo.Basic, areaId: Long, areaName: String, buildingId: Long, augmentedPopupInfo: PopupInfo.Augmented)
    fun getSimilarPopupInfos(query: String, k: Int): List<PopupInfo.WithScore>
    fun isPopupInfoPersisted(id: Long): Boolean
    fun getPopupsByAreaId(areaId: Long, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByBuildingId(buildingId: Long, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByAreaName(areaName: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByCategory(category: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByFilters(
        query: String,
        k: Int,
        areaId: Long?,
        buildingId: Long?,
        areaName: String?,
        ageGroup: String?,
        category: String?,
    ): List<PopupInfo.WithScore>
}