package com.wheretopop.domain.popup

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo

interface PopupService {
    suspend fun savePopup(popup: Popup)
}