package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId

interface PopupRepository {
    fun findById(id: PopupId): Popup?
    fun findByName(name: String): Popup?
    fun findAll(): List<Popup>
    fun save(popup: Popup): Popup
    fun save(popups: List<Popup>): List<Popup>
    fun deleteById(id: PopupId)
}