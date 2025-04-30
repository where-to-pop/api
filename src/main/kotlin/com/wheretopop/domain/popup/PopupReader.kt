package com.wheretopop.domain.popup

interface PopupReader {
    suspend fun findAll(): List<Popup>
    suspend fun findById(id: PopupId): Popup?
    suspend fun findByName(name: String): Popup?
}