package com.wheretopop.application.popup

import com.wheretopop.domain.building.BuildingCommand
import com.wheretopop.domain.building.BuildingService
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupService
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import com.wheretopop.shared.model.Location
import org.springframework.stereotype.Service

/**
 * Popup 애플리케이션 서비스
 * 컨트롤러와 도메인 서비스 사이의 퍼사드 역할
 */
@Service
class PopupFacade(
    private val popupService: PopupService,
    private val popupUseCase: PopupUseCase,
    private val buildingService: BuildingService
) {
    /**
     * 무슨 메서드가 필요할까?
     */

//    suspend fun ingestPopupExternalData() {
//        return popupPopplyUseCase.crawlPopplyAndSave()
//    }

    suspend fun ingestPopupExternalData() {
        val popplyPopupDetails = popupUseCase.crawlPopply()
        popplyPopupDetails.forEach{ popupDetail: PopupDetail ->
            if (popupDetail.latitude == null || popupDetail.longitude == null) return
            val buildingCommand = BuildingCommand.CreateBuildingCommand(popupDetail.address, Location(popupDetail.latitude, popupDetail.longitude))
            val buildingId = buildingService.getOrCreateBuildingId(buildingCommand)
            val newPopup = Popup.create(
                name = popupDetail.name,
                address = popupDetail.address,
                buildingId = buildingId
            )
            popupService.savePopup(newPopup)
            popupUseCase.savePopply(popupDetail, newPopup.id)
            popupUseCase.crawlXAndSave(newPopup)
        }
    }
}