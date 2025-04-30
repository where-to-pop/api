package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupStore
import org.springframework.stereotype.Component

@Component
class PopupStoreImpl(
    private val popupRepository: PopupRepository
) : PopupStore {

    override suspend fun save(popup: Popup): Popup {
        return popupRepository.save(popup)
    }
    override suspend fun save(popups: List<Popup>): List<Popup> {
        return popupRepository.save(popups)
    }
    override suspend fun delete(popup: Popup) {
        popupRepository.deleteById(popup.id)
    }
    override suspend fun delete(popups: List<Popup>) {
        popups.forEach { popup ->
            popupRepository.deleteById(popup.id)
        }
    }
}