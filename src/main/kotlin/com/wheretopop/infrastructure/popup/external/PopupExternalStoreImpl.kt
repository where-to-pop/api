package com.wheretopop.infrastructure.popup.external

import com.wheretopop.infrastructure.popup.external.popply.PopplyProcessor
import org.springframework.stereotype.Component

@Component
class PopupExternalStoreImpl(
    private val popplyProcessor: PopplyProcessor
): PopupExternalStore {

    override suspend fun crawlPopplyAndSave() {
        popplyProcessor.crawlAndSave()
    }
}