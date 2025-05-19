package com.wheretopop.domain.popup

import org.springframework.ai.document.Document

interface PopupVectorRepository {
    fun addPopupInfo(popupInfo: PopupInfo.Detail)
    fun findSimilarPopups(query: String): List<Document>
    fun findById(id: Long): Document?
}