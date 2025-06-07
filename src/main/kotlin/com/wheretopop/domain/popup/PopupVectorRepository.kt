package com.wheretopop.domain.popup

import org.springframework.ai.document.Document

interface PopupVectorRepository {
    fun addPopupInfo(popupInfo: PopupInfo.Detail)
    fun findSimilarPopups(query: String, k: Int): List<Document>
    fun findById(id: Long): Document?
    fun findByAreaId(areaId: Long, query: String, k: Int): List<Document>
    fun findByBuildingId(buildingId: Long, query: String, k: Int): List<Document>
    fun findByAreaName(areaName: String, query: String, k: Int): List<Document>
    fun findByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<Document>
    fun findByCategory(category: String, query: String, k: Int): List<Document>
}