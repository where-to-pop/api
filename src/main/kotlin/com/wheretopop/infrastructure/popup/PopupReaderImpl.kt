package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupReader
import org.springframework.stereotype.Component

@Component
class PopupReaderImpl(
    private val popupRepository: PopupRepository
) : PopupReader {

    override fun findAll(): List<Popup> {
        return popupRepository.findAll()
    }

    override fun findById(id: PopupId): Popup? {
        return popupRepository.findById(id)
    }

    override fun findByName(name: String): Popup? {
        return popupRepository.findByName(name)
    }
}