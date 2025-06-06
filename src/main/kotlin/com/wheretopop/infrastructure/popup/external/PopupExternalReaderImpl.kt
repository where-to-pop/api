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

    override fun getSimilarPopups(query: String): List<PopupInfo.WithScore> {
        return popplyProcessor.getSimilarPopups(query)
    }

    override fun isPopupInfoPersisted(id: Long): Boolean {
        return popplyProcessor.existsById(id)
    }

    override fun getPopupsForSpecificAreaById(areaId: Long, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByAreaId(areaId, k)
    }

    override fun getPopupsForSpecificBuildingById(buildingId: Long, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByBuildingId(buildingId, k)
    }

    override fun getPopupsForSpecificAreaByName(areaName: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByAreaName(areaName, k)
    }

    override fun getPopupsForSpecificAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByTargetAgeGroup(ageGroup, query, k)
    }

    override fun getPopupsForSpecificCategory(category: String, k: Int): List<PopupInfo.WithScore> {
        return popplyProcessor.getPopupsByCategory(category, k)
    }
}