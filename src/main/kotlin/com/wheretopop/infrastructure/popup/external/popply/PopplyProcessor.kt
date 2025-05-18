package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoWithScore

/**
 * 팝플리에서 정보를 크롤링하고, 이를 가공하여 Popup 도메인에 맞는 형태로 변환하는 책임
 */
interface PopplyProcessor {
    fun crawlAndSave()
    fun crawl(): List<PopupDetail>
    fun save(popupDetail: PopupDetail, popupId: PopupId)
    fun saveEmbeddings(popupInfos: List<PopupInfo>)
    fun getAllPopups(): List<PopupInfo>
    fun getSiliarPopups(query: String): List<PopupInfoWithScore>
}