package com.wheretopop.domain.popup

import org.springframework.ai.document.Document

interface PopupVectorRepository {
    fun addPopupInfo(popupInfo: PopupInfo.Detail)
    fun findSimilarPopups(query: String): List<Document>
    fun findById(id: Long): Document?
    fun findByAreaId(areaId: Long, k: Int): List<Document>
    fun findByBuildingId(buildingId: Long, k: Int): List<Document>
    fun findByAreaName(areaName: String, k: Int): List<Document>
    fun findByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<Document>
    fun findByCategory(category: String, k: Int): List<Document>
}