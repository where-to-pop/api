package com.wheretopop.application.popup

import com.wheretopop.domain.popup.Popup

interface XUseCase {
    fun crawlXAndSave(popup: Popup)
}