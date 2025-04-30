package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupService
import org.springframework.stereotype.Service

/**
 * Popup 애플리케이션 서비스
 * 컨트롤러와 도메인 서비스 사이의 퍼사드 역할
 */
@Service
class PopupFacade(
    private val popupService: PopupService,
    private val popupPopplyUseCase: PopupPopplyUseCase
) {
    /**
     * 무슨 메서드가 필요할까?
     */

    suspend fun ingestPopupExternalData() {
        return popupPopplyUseCase.crawlPopplyAndSave()
    }
}