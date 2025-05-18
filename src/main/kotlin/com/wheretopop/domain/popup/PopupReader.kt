package com.wheretopop.domain.popup

interface PopupReader {
    fun findAll(): List<Popup>
    fun findById(id: PopupId): Popup?
    fun findByName(name: String): Popup?
}