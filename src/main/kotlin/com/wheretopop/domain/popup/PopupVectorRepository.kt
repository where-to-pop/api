package com.wheretopop.domain.popup

import org.springframework.ai.document.Document

interface PopupVectorRepository {
    fun addPopupInfos(popupInfos: List<PopupInfo>)
    fun findSimilarPopups(query: String): List<Document>
}