package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopplyProcessor
import org.springframework.stereotype.Component

@Component
class PopupExternalReaderImpl(
    private val popplyProcessor: PopplyProcessor,
): PopupExternalReader {
    override fun getAllPopply(): List<PopupInfo.Basic> {
        return popplyProcessor.getAllPopups()
    }

    override fun getSimilarPopups(query: String): List<PopupInfo.WithScore> {
        return popplyProcessor.getSimilarPopups(query)
    }

    override fun isPopupInfoPersisted(id: Long): Boolean {
        return popplyProcessor.existsById(id)
    }
}