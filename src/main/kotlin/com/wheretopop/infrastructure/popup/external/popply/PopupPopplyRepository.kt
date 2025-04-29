package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId

interface PopupPopplyRepository {
    suspend fun save(entity: PopupPopplyEntity): PopupPopplyEntity
    suspend fun save(entities: List<PopupPopplyEntity>): List<PopupPopplyEntity>
    suspend fun findByPopplyId(popplyId: Int): PopupPopplyEntity?
    suspend fun findByPopupId(popupId: PopupId): PopupPopplyEntity?
}