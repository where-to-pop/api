package com.wheretopop.application.popup

import java.time.LocalDate

interface PopupReviewUseCase {
    suspend fun syncReviewsToVectorDB(targetDate: LocalDate? = LocalDate.now(), sourceFilter: String? = null)
}