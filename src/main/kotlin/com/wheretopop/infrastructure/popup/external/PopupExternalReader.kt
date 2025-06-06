package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo

interface PopupExternalReader {
    fun getAllPopply():List<PopupInfo.Basic>
    fun getSimilarPopups(query: String): List<PopupInfo.WithScore>
    fun isPopupInfoPersisted(id: Long): Boolean
    fun getPopupsForSpecificAreaById(areaId: Long, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificBuildingById(buildingId: Long, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificAreaByName(areaName: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsForSpecificCategory(category: String, k: Int): List<PopupInfo.WithScore>
}