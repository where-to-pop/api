package com.wheretopop.infrastructure.popup.external

import com.wheretopop.application.popup.PopupPopplyUseCase
import org.springframework.stereotype.Component

@Component
class PopupExternalManager(
    private val popupExternalStore: PopupExternalStore
): PopupPopplyUseCase {
    override suspend fun crawlPopplyAndSave() {
        return popupExternalStore.crawlPopplyAndSave()
    }
}