package com.wheretopop.domain.popup

import java.time.LocalDate

interface PopupReviewRepository {
    suspend fun findAllByCreatedAt(date: LocalDate?): List<PopupReview>
}