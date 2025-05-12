package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopplyProcessor
import org.springframework.stereotype.Component

@Component
class PopupExternalReaderImpl(
    private val popplyProcessor: PopplyProcessor,
): PopupExternalReader {
    override suspend fun getAllPopply(): List<PopupInfo> {
        return popplyProcessor.getAllPopups()
    }
}