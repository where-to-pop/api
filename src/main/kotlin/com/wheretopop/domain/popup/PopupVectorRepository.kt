package com.wheretopop.domain.popup

import org.springframework.ai.document.Document

interface PopupVectorRepository {
    suspend fun addPopupInfos(popupInfos: List<PopupInfo>)
    suspend fun findSimilarPopups(query: String): List<Document>
}