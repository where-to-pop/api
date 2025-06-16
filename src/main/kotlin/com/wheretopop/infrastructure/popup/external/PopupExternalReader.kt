package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo

interface PopupExternalReader {
    fun getAllPopply():List<PopupInfo.Basic>
    fun getSimilarPopups(query: String, k: Int): List<PopupInfo.WithScore>
    fun isPopupInfoPersisted(id: Long): Boolean
    fun getPopupsForSpecificAreaById(areaId: Long, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificBuildingById(buildingId: Long, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificAreaByName(areaName: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificCategory(category: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificFilters(
        query: String,
        k: Int,
        areaId: Long?,
        buildingId: Long?,
        areaName: String?,
        ageGroup: String?,
        category: String?,
    ): List<PopupInfo.WithScore>
}