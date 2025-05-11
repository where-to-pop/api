package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo

interface PopupPopplyRepository {
    suspend fun save(entity: PopupPopplyEntity): PopupPopplyEntity
    suspend fun save(entities: List<PopupPopplyEntity>): List<PopupPopplyEntity>
    suspend fun findAll(): List<PopupInfo>
    suspend fun findByPopplyId(popplyId: Int): PopupPopplyEntity?
    suspend fun findByPopupId(popupId: PopupId): PopupPopplyEntity?
}