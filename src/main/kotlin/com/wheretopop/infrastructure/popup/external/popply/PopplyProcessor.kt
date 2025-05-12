package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo

/**
 * 팝플리에서 정보를 크롤링하고, 이를 가공하여 Popup 도메인에 맞는 형태로 변환하는 책임
 */
interface PopplyProcessor {
    suspend fun crawlAndSave()
    suspend fun crawl(): List<PopupDetail>
    suspend fun save(popupDetail: PopupDetail, popupId: PopupId)
    suspend fun saveEmbeddings(popupInfos: List<PopupInfo>)
    suspend fun getAllPopups(): List<PopupInfo>
}