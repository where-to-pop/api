package com.wheretopop.domain.popup

interface PopupStore {
    fun save(popup: Popup): Popup
    fun save(popups: List<Popup>): List<Popup>
    fun delete(popup: Popup)
    fun delete(popups: List<Popup>)
}