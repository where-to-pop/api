package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.shared.domain.identifier.PopupPopplyId
import com.wheretopop.shared.infrastructure.entity.PopupPopplyEntity

interface PopupPopplyRepository {
    fun save(entity: PopupPopplyEntity): PopupPopplyEntity
    fun save(entities: List<PopupPopplyEntity>): List<PopupPopplyEntity>
    fun findAll(): List<PopupInfo.Basic>
    fun findByPopplyId(popplyId: PopupPopplyId): PopupPopplyEntity?
    fun findByPopupId(popupId: PopupId): PopupPopplyEntity?
}