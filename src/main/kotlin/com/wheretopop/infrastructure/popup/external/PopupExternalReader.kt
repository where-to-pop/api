package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoWithScore

interface PopupExternalReader {
    fun getAllPopply():List<PopupInfo>
    fun getSimilarPopups(query: String): List<PopupInfoWithScore>
}