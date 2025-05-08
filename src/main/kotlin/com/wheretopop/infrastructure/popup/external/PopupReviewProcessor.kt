package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.Popup
import java.time.LocalDate

interface PopupReviewProcessor {
    suspend fun crawlAndSaveByPopup(popup: Popup)
    suspend fun searchByDate(targetDate: LocalDate)
}