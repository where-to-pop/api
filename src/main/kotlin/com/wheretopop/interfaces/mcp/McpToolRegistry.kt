package com.wheretopop.interfaces.mcp

import com.wheretopop.application.popup.PopupFacade
import com.wheretopop.domain.popup.PopupInfo
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class McpToolRegistry(
    private val popupFacade: PopupFacade
) {
    @Tool(description = "returns current local date time.")
    fun now(): String {
        return "today is 2023-10-01 and 12:00"
    }

    @Tool(description = """사용자의 텍스트 검색어(query)와 유사한 팝업 스토어 또는 이벤트 정보를 요약된 설명 문자열 형태로 반환합니다.
            이 문자열에는 검색된 각 팝업의 주요 정보(이름, 설명, 주소, 유사도 점수 등)가 포함되며,
            이 정보를 바탕으로 사용자가 다음 단계로 활용할 수 있는 추천 Tool 사용법에 대한 가이드도 함께 제공됩니다.
            예를 들어, 특정 팝업의 더 자세한 정보를 얻거나 위치를 찾는 방법을 안내할 수 있습니다.
            쿼리에는 찾고 있는 팝업의 특징, 주제, 또는 관심 있는 팝업의 예시 등을 명시할 수 있습니다.""")
    suspend fun getSimilarPopupsByQuery(query: String): String {
        val popupsWithScores: List<PopupInfo.WithScore> = popupFacade.findSimilarPopupInfos(query)
        if (popupsWithScores.isEmpty()) {
            return "입력하신 '${query}'와(과) 유사한 팝업 정보를 찾을 수 없었습니다. 다른 검색어를 사용해 보세요."
        }

        val popupsDetailsString = popupsWithScores.joinToString(separator = "\n\n====================\n\n") { item ->
            val popup = item.popup
            """
            [팝업 정보]
            - 이름: ${popup.name} (ID: ${popup.id})
            - 유사도 점수: ${String.format("%.2f", item.score)}
            - 설명: ${popup.description.take(150)}${if (popup.description.length > 150) "..." else ""}
            - 주소: ${popup.address}
            - 주최자: ${popup.organizerName}
            """.trimIndent()
        }

        val followupActionsGuide = """
        이 정보를 활용하여 다음과 같은 추가 작업을 고려해 볼 수 있습니다:
        1. 특정 팝업에 대한 전체 상세 정보 확인:
           - 각 팝업의 'ID'를 사용하여 `getPopupDetailsById(id = 팝업ID)` Tool을 호출하여 더 자세한 정보를 얻을 수 있습니다.
        2. 팝업 위치 길찾기 또는 주변 정보 탐색:
           - 팝업의 '주소' 정보를 활용하여 지도 서비스를 이용하거나, '주소' 또는 'ID'를 `getNavigationInfo(identifier = 주소또는ID)` Tool에 전달하여 관련 정보를 요청할 수 있습니다.
        3. 다른 조건으로 재검색:
           - 현재 결과가 만족스럽지 않다면, 다른 키워드로 이 `getSimilarPopupsByQuery` Tool을 다시 호출해 보세요.
    
        (참고: 여기에 언급된 `getPopupDetailsById`, `getNavigationInfo` 등의 Tool 이름과 파라미터는 예시입니다. 실제 사용 가능한 Tool의 정확한 명세를 확인 후 사용해 주세요.)
        """.trimIndent()

        return """
        '${query}' 관련 유사 팝업 검색 결과입니다:
    
        ${popupsDetailsString}
    
        ====================
    
        ${followupActionsGuide}
        """.trimIndent()
    }

}