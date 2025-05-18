package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupStore
import org.springframework.stereotype.Component

@Component
class PopupStoreImpl(
    private val popupRepository: PopupRepository
) : PopupStore {

    override fun save(popup: Popup): Popup {
        return popupRepository.save(popup)
    }
    override fun save(popups: List<Popup>): List<Popup> {
        return popupRepository.save(popups)
    }
    override fun delete(popup: Popup) {
        popupRepository.deleteById(popup.id)
    }
    override fun delete(popups: List<Popup>) {
        popups.forEach { popup ->
            popupRepository.deleteById(popup.id)
        }
    }
}