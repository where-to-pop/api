package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo

/**
 * 팝플리에서 정보를 크롤링하고, 이를 가공하여 Popup 도메인에 맞는 형태로 변환하는 책임
 */
interface PopplyProcessor {
    fun crawlAndSave()
    fun crawl(): List<PopupDetail>
    fun save(popupDetail: PopupDetail, popupId: PopupId)
    fun saveEmbeddings(popupInfo: PopupInfo.Detail)
    fun getAllPopups(): List<PopupInfo.Basic>
    fun getSimilarPopups(query: String, k: Int): List<PopupInfo.WithScore>
    fun existsById(id: Long): Boolean
    fun getPopupsByAreaId(areaId: Long, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByBuildingId(buildingId: Long, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByAreaName(areaName: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore>
    fun getPopupsByCategory(category: String, query: String, k: Int): List<PopupInfo.WithScore>
}