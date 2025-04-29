package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId

interface PopupRepository {
    suspend fun findById(id: PopupId): Popup?
    suspend fun findByName(name: String): Popup?
    suspend fun findAll(): List<Popup>
    suspend fun save(popup: Popup): Popup
    suspend fun save(popups: List<Popup>): List<Popup>
    suspend fun deleteById(id: PopupId)
}