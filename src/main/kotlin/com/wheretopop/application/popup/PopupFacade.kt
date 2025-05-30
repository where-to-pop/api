package com.wheretopop.application.popup

import com.wheretopop.application.building.BuildingFacade
import com.wheretopop.domain.area.AreaService
import com.wheretopop.domain.building.BuildingCommand
import com.wheretopop.domain.building.BuildingService
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupService
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import com.wheretopop.shared.model.Location
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Popup 애플리케이션 서비스
 * 컨트롤러와 도메인 서비스 사이의 퍼사드 역할
 */
@Service
class PopupFacade(
    private val popupService: PopupService,
    private val buildingFacade: BuildingFacade,
    private val popplyUseCase: PopplyUseCase,
    private val xUseCase: XUseCase,
    private val areaService: AreaService,
    private val buildingService: BuildingService,
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    fun ingestPopupExternalData() {
        val popplyPopupDetails = popplyUseCase.crawlPopply()
        popplyPopupDetails.forEach{ popupDetail: PopupDetail ->
            if (popupDetail.latitude == null || popupDetail.longitude == null) return
            val buildingCommand = BuildingCommand.CreateBuildingCommand(popupDetail.address, Location(popupDetail.latitude, popupDetail.longitude))
            val buildingId = buildingFacade.getOrCreateBuildingId(buildingCommand)
            val newPopup = Popup.create(
                name = popupDetail.name,
                address = popupDetail.address,
                buildingId = buildingId
            )
            popupService.savePopup(newPopup)
            popplyUseCase.savePopply(popupDetail, newPopup.id)
            xUseCase.crawlXAndSave(newPopup)
        }
    }

    fun processPopupInfosForVectorSearch() {
        val popplyPopupInfos = popplyUseCase.getPopplyList()
        popplyPopupInfos.forEach { basicPopupInfo ->
//            if (popplyUseCase.isPopupInfoPersisted(basicPopupInfo.id.value)) return@forEach

            if (basicPopupInfo.latitude == null || basicPopupInfo.longitude == null) return@forEach
            val areaFound = areaService.findNearestArea(
                basicPopupInfo.latitude,
                basicPopupInfo.longitude,
            )
            if (areaFound == null) return@forEach
            val buildingFound = buildingService.findBuildingByAddress(basicPopupInfo.address)
            popplyUseCase.embedAndSavePopupInfo(
                basicPopupInfo,
                areaFound.id.toLong(),
                areaFound.name,
                buildingFound.id.toLong(),
            )
        }
    }

    fun findSimilarPopupInfos(query: String): List<PopupInfo.WithScore> {
        return popplyUseCase.getSimilarPopupInfos(query)
    }
}