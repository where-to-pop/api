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
    override fun embedAndSavePopupInfo(popupInfo: PopupInfo.Basic, areaId: Long, areaName: String, buildingId: Long, augmentedPopupInfo: PopupInfo.Augmented) {
        val detailedPopupInfo = PopupInfoMapper.toDetail(popupInfo, areaId, areaName, buildingId, augmentedPopupInfo)
        return popupExternalStore.embedAndSaveDetailedPopupInfo(detailedPopupInfo)
    }
    override fun getSimilarPopupInfos(query: String, k: Int): List<PopupInfo.WithScore> {
        return popupExternalReader.getSimilarPopups(query, k)
    }

    override fun isPopupInfoPersisted(id: Long): Boolean {
        return popupExternalReader.isPopupInfoPersisted(id)
    }

    override fun getPopupsByAreaId(areaId: Long, query: String, k: Int): List<PopupInfo.WithScore> {
        return popupExternalReader.getPopupsForSpecificAreaById(areaId,query, k)
    }

    override fun getPopupsByBuildingId(buildingId: Long, query: String, k: Int): List<PopupInfo.WithScore> {
        return popupExternalReader.getPopupsForSpecificBuildingById(buildingId,query, k)
    }

    override fun getPopupsByAreaName(areaName: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popupExternalReader.getPopupsForSpecificAreaByName(areaName,query, k)
    }

    override fun getPopupsByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popupExternalReader.getPopupsForSpecificAgeGroup(ageGroup, query, k)
    }

    override fun getPopupsByCategory(category: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popupExternalReader.getPopupsForSpecificCategory(category,query, k)
    }

    override fun getPopupsByFilters(
        query: String,
        k: Int,
        areaId: Long?,
        buildingId: Long?,
        areaName: String?,
        ageGroup: String?,
        category: String?,
    ): List<PopupInfo.WithScore> {
        return popupExternalReader.getPopupsForSpecificFilters(query, k, areaId, buildingId, areaName, ageGroup, category)
    }
}