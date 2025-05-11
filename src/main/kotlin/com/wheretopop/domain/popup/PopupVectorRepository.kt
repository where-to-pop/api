package com.wheretopop.domain.popup

interface PopupVectorRepository {
    suspend fun addPopupInfos(popupInfos: List<PopupInfo>)
}