package com.wheretopop.infrastructure.popup.external

interface PopupExternalStore {
    suspend fun crawlPopplyAndSave()
}