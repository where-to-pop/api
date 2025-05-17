package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoWithScore

interface PopupExternalReader {
    suspend fun getAllPopply():List<PopupInfo>
    suspend fun getSimilarPopups(query: String): List<PopupInfoWithScore>
}