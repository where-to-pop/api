package com.wheretopop.infrastructure.popup.external.x

import com.wheretopop.infrastructure.popup.PopupEntity

/**
 * 팝업 도메인 엔티티를 받아 X 데이터를 크롤링해 저장
 */
interface XProcessor {
    suspend fun crawlAndSaveByPopup(popup: PopupEntity)
}