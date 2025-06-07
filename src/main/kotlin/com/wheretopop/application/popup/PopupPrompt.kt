package com.wheretopop.application.popup

import com.wheretopop.domain.popup.PopupInfo
import org.springframework.stereotype.Component

@Component
class PopupPrompt() {
    companion object {
        fun getAdditionalSystemPrompt(popupInfoText: String): String {
            return """
            다음 팝업스토어 소개 글을 바탕으로 다음 정보를 추출해주세요:

            [입력 텍스트]
            $popupInfoText

            [목표]
            아래 정보를 JSON 형태로 추출해주세요:

            1. "keywords": 이 팝업스토어를 대표할 수 있는 핵심 키워드 3~5개 (ex. 레트로, 디자인, 굿즈 등) 단, 팝업스토어는 키워드가 될 수 없다.
            2. "category": 다음 enum 중 하나를 고르세요 (값은 enum 이름):
               - FASHION, FOOD_AND_BEVERAGE, BEAUTY, ART, CHARACTER, MEDIA, OTHER
            3. "targetAgeGroup": 이 팝업의 주요 타겟 연령층을 아래 enum 중에서 하나를 골라 주세요:
               - TEEN_AND_UNDER, TWENTIES, THIRTIES, FORTIES, FIFTY_AND_OVER
            4. "brandKeywords": 브랜드의 특성 또는 정체성을 나타내는 키워드 2~4개 (ex. 친환경, 일러스트, 캐릭터, 핸드메이드 등) 단, 브랜드는 키워드가 될 수 없다.

            [출력 예시]
            ```json
            {
              "keywords": ["레트로", "굿즈", "운동화", "체험형", "키치"],
              "category": "FASHION",
              "targetAgeGroup": "TWENTIES",
              "brandKeywords": ["패션", "디자인", "힙한", "친환경"]
            }
        """.trimIndent()
        }
    }
}