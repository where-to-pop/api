package com.wheretopop.interfaces.popup

import com.wheretopop.application.popup.PopupFacade
import com.wheretopop.domain.popup.PopupInfo
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class PopupToolRegistry(
    private val popupFacade: PopupFacade
) {
    private val DEFAULT_K = 3

    private fun formatPopupDetails(popupsWithScores: List<PopupInfo.WithScore>): String {
        if (popupsWithScores.isEmpty()) {
            return "관련된 팝업 정보를 찾을 수 없습니다."
        }

        return popupsWithScores.joinToString(separator = "\n\n====================\n\n") { item ->
            val popup = item.popup
            val truncatedDescription = popup.description.take(150) + if (popup.description.length > 150) "..." else ""

            """
        [팝업 정보]
        - 이름: ${popup.name} (ID: ${popup.id})
        - 유사도 점수: ${String.format("%.2f", item.score)}
        - 카테고리: ${popup.category}
        - 설명: $truncatedDescription
        - 주소: ${popup.address} (건물 ID: ${popup.buildingId})
        - 브랜드: ${popup.organizerName} (브랜드 키워드: ${popup.brandKeywords.toString()}
        - 지역: ${popup.areaName} (지역 ID: ${popup.areaId})
        - 타겟 연령층: ${popup.targetAgeGroups}
        - 키워드: ${popup.keywords.toString()}
        """.trimIndent()
        }
    }

    @Tool(description = "Finds popups similar to the given query. The 'query' parameter describes the desired characteristics or information of the popup (e.g., 'sticker event', 'cooking class'). The 'k' parameter specifies the maximum number of results to return (defaults to 2).")
    fun findSimilarPopupInfos(query: String, k: Int = DEFAULT_K): String {
        val popups = popupFacade.findSimilarPopupInfos(query, k)
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popup events by area ID. The 'query' parameter is optional and can provide additional characteristics for more specific searches. The 'k' parameter specifies the maximum number of results to return (defaults to 2). Use when the user gives a specific area ID and wants to see related popups.")
    fun findPopupInfosByAreaId(areaId: Long, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByAreaId(areaId, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popup events by building ID. The 'query' parameter is optional and can provide additional characteristics for more specific searches. The 'k' parameter specifies the maximum number of results to return (defaults to 2). Use when the user asks for events in a specific building by ID.")
    fun findPopupInfosByBuildingId(buildingId: Long, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByBuildingId(buildingId, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popup events by area name. The 'query' parameter is optional and can provide additional characteristics for more specific searches. The 'k' parameter specifies the maximum number of results to return (defaults to 2). Use when the user gives a name of an area (e.g., '홍대, 건대, 강남') to find related popups.")
    fun findPopupInfosByAreaName(areaName: String, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByAreaName(areaName, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(
        description = """
            Finds popup events by target age group and optional query.
            The 'query' parameter is optional and can provide additional characteristics for more specific searches.
            The 'k' parameter specifies the maximum number of results to return (defaults to 2).
            Use when the user wants popups for specific age groups.
            The ageGroup parameter **must be one of the following enum values**:
            - TEEN_AND_UNDER
            - TWENTIES
            - THIRTIES
            - FORTIES
            - FIFTY_AND_OVER

            Example queries: "Find popups for teens", "Show me popups for people in their 20s"
        """
    )
    fun findPopupInfosByTargetAgeGroup(ageGroup: String, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByTargetAgeGroup(ageGroup, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(
        description = """
            Finds popup events by category.
            The 'query' parameter is optional and can provide additional characteristics for more specific searches.
            The 'k' parameter specifies the maximum number of results to return (defaults to 2).
            The category parameter **must be one of the following enum values**:
            - FASHION
            - FOOD_AND_BEVERAGE
            - BEAUTY
            - ART
            - CHARACTER
            - MEDIA
            - OTHER

            Use this tool when the user mentions a specific category, like 'Show me art-related popups' or 'Find food popups'.
        """
    )fun findPopupInfosByCategory(category: String, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByCategory(category, query, k)
        return formatPopupDetails(popups)
    }
}