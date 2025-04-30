package com.wheretopop.domain.popup

interface PopupStore {
    suspend fun save(popup: Popup): Popup
    suspend fun save(popups: List<Popup>): List<Popup>
    suspend fun delete(popup: Popup)
    suspend fun delete(popups: List<Popup>)
}