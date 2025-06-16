package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopplyProcessor
import org.springframework.stereotype.Component

@Component
class PopupExternalReaderImpl(
    private val popplyProcessor: PopplyProcessor,
): PopupExternalReader {
    override fun getAllPopply(): List<PopupInfo.Basic> {
        return popplyProcessor.getAllPopups()
    }

    override fun getSimilarPopups(query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getSimilarPopups(query, k)
    }

    override fun isPopupInfoPersisted(id: Long): Boolean {
        return popplyProcessor.existsById(id)
    }

    override fun getPopupsForSpecificAreaById(areaId: Long, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByAreaId(areaId,query, k)
    }

    override fun getPopupsForSpecificBuildingById(buildingId: Long, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByBuildingId(buildingId,query, k)
    }

    override fun getPopupsForSpecificAreaByName(areaName: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByAreaName(areaName,query, k)
    }

    override fun getPopupsForSpecificAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByTargetAgeGroup(ageGroup, query, k)
    }

    override fun getPopupsForSpecificCategory(category: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByCategory(category,query, k)
    }

    override fun getPopupsForSpecificFilters(
        query: String,
        k: Int,
        areaId: Long?,
        buildingId: Long?,
        areaName: String?,
        ageGroup: String?,
        category: String?,
    ): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByFilters(query, k, areaId, buildingId, areaName, ageGroup, category)
    }
}