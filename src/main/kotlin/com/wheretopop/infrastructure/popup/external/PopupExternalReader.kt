package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo

interface PopupExternalReader {
    suspend fun getAllPopply():List<PopupInfo>
    suspend fun getSimilarPopups(query: String)
}