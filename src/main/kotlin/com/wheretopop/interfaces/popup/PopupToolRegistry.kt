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
        - 타겟 연령층: ${popup.targetAgeGroup}
        - 키워드: ${popup.keywords.toString()}
        """.trimIndent()
        }
    }
    @Tool(
        description = """
        여러 조건(areaName, category, ageGroup 등)으로 팝업을 복합적으로 검색합니다.
        - areaName 파라미터를 사용할 경우, 아래 값 중 하나여야 합니다:
          "강남 MICE 관광특구", "동대문 관광특구", "명동 관광특구", "이태원 관광특구", "잠실 관광특구", "홍대 관광특구",
          "강남역", "건대입구역", "고속터미널역", "사당역", "서울역", "선릉역", "신촌·이대역", "충정로역", "합정역", "혜화역",
          "가로수길", "북촌한옥마을", "서촌", "성수카페거리", "압구정로데오거리", "여의도", "연남동", "영등포 타임스퀘어",
          "용리단길", "인사동·익선동", "해방촌·경리단길", "광화문광장"
        - category 파라미터를 사용할 경우, 아래 enum 값 중 하나여야 합니다:
          FASHION, FOOD_AND_BEVERAGE, BEAUTY, ART, CHARACTER, MEDIA, OTHER
        - ageGroup 파라미터를 사용할 경우, 아래 enum 값 중 하나여야 합니다:
          TEEN_AND_UNDER, TWENTIES, THIRTIES, FORTIES, FIFTY_AND_OVER
        'query'는 추가적인 검색 특성을 자연어로 입력합니다.
        'k'는 최대 반환 개수(기본값 3)입니다.
        예시: "areaName='홍대 관광특구', category='FASHION', ageGroup='TWENTIES', query='전시'"
    """
    )
    fun findPopupInfosByFilters(
        query: String = "",
        k: Int = DEFAULT_K,
        areaId: Long? = null,
        buildingId: Long? = null,
        areaName: String? = null,
        ageGroup: String? = null,
        category: String? = null
    ): String {
        val popups = popupFacade.findPopupInfosByFilters(
            query = query,
            k = k,
            areaId = areaId,
            buildingId = buildingId,
            areaName = areaName,
            ageGroup = ageGroup,
            category = category
        )
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popups similar to the given query. The 'query' parameter describes the desired characteristics or information of the popup (e.g., 'sticker event', 'cooking class'). The 'k' parameter specifies the maximum number of results to return (defaults to 3).")
    fun findSimilarPopupInfos(query: String, k: Int = DEFAULT_K): String {
        val popups = popupFacade.findSimilarPopupInfos(query, k)
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popup events by area ID. The 'query' parameter is optional and can provide additional characteristics for more specific searches. The 'k' parameter specifies the maximum number of results to return (defaults to 3). Use when the user gives a specific area ID and wants to see related popups.")
    fun findPopupInfosByAreaId(areaId: Long, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByAreaId(areaId, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popup events by building ID. The 'query' parameter is optional and can provide additional characteristics for more specific searches. The 'k' parameter specifies the maximum number of results to return (defaults to 3). Use when the user asks for events in a specific building by ID.")
    fun findPopupInfosByBuildingId(buildingId: Long, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByBuildingId(buildingId, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(description = "Finds popup events by area name. The 'query' parameter is optional and can provide additional characteristics for more specific searches. The 'k' parameter specifies the maximum number of results to return (defaults to 3). Use when the user gives a name of an area (e.g., '홍대, 건대, 강남') to find related popups.")
    fun findPopupInfosByAreaName(areaName: String, query: String = "", k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByAreaName(areaName, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(
        description = """
            Finds popup events by target age group and optional query.
            The 'query' parameter is optional and can provide additional characteristics for more specific searches.
            The 'k' parameter specifies the maximum number of results to return (defaults to 3).
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
    fun findPopupInfosByTargetAgeGroup(ageGroup: String, query: String, k: Int = DEFAULT_K): String {
        val popups = popupFacade.findPopupInfosByTargetAgeGroup(ageGroup, query, k)
        return formatPopupDetails(popups)
    }

    @Tool(
        description = """
            Finds popup events by category.
            The 'query' parameter is optional and can provide additional characteristics for more specific searches.
            The 'k' parameter specifies the maximum number of results to return (defaults to 3).
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
