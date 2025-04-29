package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupReader
import org.springframework.stereotype.Component

@Component
class PopupReaderImpl(
    private val popupRepository: PopupRepository
) : PopupReader {

    override suspend fun findAll(): List<Popup> {
        return popupRepository.findAll()
    }

    override suspend fun findById(id: PopupId): Popup? {
        return popupRepository.findById(id)
    }

    override suspend fun findByName(name: String): Popup? {
        return popupRepository.findByName(name)
    }
}