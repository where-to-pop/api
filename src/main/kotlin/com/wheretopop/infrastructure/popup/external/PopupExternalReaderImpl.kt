package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoWithScore
import com.wheretopop.infrastructure.popup.external.popply.PopplyProcessor
import org.springframework.stereotype.Component

@Component
class PopupExternalReaderImpl(
    private val popplyProcessor: PopplyProcessor,
): PopupExternalReader {
    override fun getAllPopply(): List<PopupInfo> {
        return popplyProcessor.getAllPopups()
    }

    override fun getSimilarPopups(query: String): List<PopupInfoWithScore> {
        return popplyProcessor.getSiliarPopups(query)
    }
}