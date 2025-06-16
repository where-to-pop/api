package com.wheretopop.application.popup

import com.google.gson.Gson
import com.wheretopop.application.building.BuildingFacade
import com.wheretopop.domain.area.AreaService
import com.wheretopop.domain.building.BuildingCommand
import com.wheretopop.domain.building.BuildingService
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupService
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.model.Location
import com.wheretopop.shared.response.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
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
    private val chatModel: ChatModel,
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

            // popup data pumping
            val inputText = PopupPrompt.getAdditionalSystemPrompt(basicPopupInfo.getContent())
            val chatOption = ChatOptions.builder().temperature(0.2).build()
            val prompt = Prompt(inputText, chatOption)
            val chatResponse = chatModel.call(prompt) ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            val augmentedPopupJson = chatResponse.result.output.text

            logger.info(augmentedPopupJson)
            val augmentedPopupInfo = Gson().fromJson(augmentedPopupJson, PopupInfo.Augmented::class.java)

            popplyUseCase.embedAndSavePopupInfo(
                basicPopupInfo,
                areaFound.id.toLong(),
                areaFound.name,
                buildingFound.id.toLong(),
                augmentedPopupInfo,
            )
        }
    }

    fun findSimilarPopupInfos(query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyUseCase.getSimilarPopupInfos(query, k)
    }

    fun findPopupInfosByAreaId(areaId: Long, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyUseCase.getPopupsByAreaId(areaId,query, k)
    }

    fun findPopupInfosByBuildingId(buildingId: Long, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyUseCase.getPopupsByBuildingId(buildingId,query, k)
    }

    fun findPopupInfosByAreaName(areaName: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyUseCase.getPopupsByAreaName(areaName,query, k)
    }

    fun findPopupInfosByTargetAgeGroup(ageGroup: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyUseCase.getPopupsByTargetAgeGroup(ageGroup, query, k)
    }

    fun findPopupInfosByCategory(category: String, query: String, k: Int): List<PopupInfo.WithScore> {
        return popplyUseCase.getPopupsByCategory(category,query, k)
    }

    fun findPopupInfosByFilters(
        query: String,
        k: Int,
        areaId: Long?,
        buildingId: Long?,
        areaName: String?,
        ageGroup: String?,
        category: String?,
    ): List<PopupInfo.WithScore> {
        return popplyUseCase.getPopupsByFilters(query, k, areaId, buildingId, areaName, ageGroup, category)
    }
}