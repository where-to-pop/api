package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo

interface PopupExternalReader {
    fun getAllPopply():List<PopupInfo.Basic>
    fun getSimilarPopups(query: String): List<PopupInfo.WithScore>
    fun isPopupInfoPersisted(id: Long): Boolean
}