package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.shared.enums.ChatMessageRole
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

/**
 * 사용자의 요청을 이해하고, 적절한 도구를 활용하여 팝업 스토어 장소 추천을 돕는 AI 어시스턴트의 프롬프트 전략.
 * 단계적 사고와 능동적인 Tool Chaining을 통해 사용자 목표 달성을 지원한다.
 */
@Component
class ConversationPromptStrategy : ChatPromptStrategy {
    private val requirements = setOf(
        PromptContextRequirement.CHAT,
        PromptContextRequirement.USER_MESSAGE,
    )

    override fun getRequirements(): Set<PromptContextRequirement> = requirements

    override fun canHandle(context: PromptContext): Boolean {
        // 사용자 메시지가 있을 때 이 전략을 사용
        return context.message?.role == ChatMessageRole.USER
    }

    override suspend fun buildPrompt(context: PromptContext): Prompt {
        require(canHandle(context)) { "Context requirements not met for ConversationPromptStrategy" }

        val systemInstructions = """
        당신은 사용자가 최적의 팝업 스토어 위치를 찾도록 돕는 전문 AI 어시스턴트 'PopVision'입니다.
        당신의 주요 목표는 사용자의 요구사항을 정확히 파악하고, 제공된 도구(Tools)를 효과적으로 활용하여 최적의 장소를 추천하는 것입니다.

        **작업 수행 가이드라인:**
        1.  **요청 이해 및 명확화:**
            *   사용자의 현재 요청을 이전 대화 맥락과 함께 깊이 이해합니다. 모호한 부분이 있다면, 명확한 답변을 위해 구체적인 질문을 하세요.
            *   **자연어 이해**: 사용자가 지역명이나 특정 장소를 자연어로 언급하면, 이전에 조회된 지역 목록(예: 'findAllArea' 도구 사용 결과)에서 해당 이름과 가장 일치하거나 유사한 지역을 **내부적으로 찾아 그 지역의 식별자(ID)로 변환**하여 관련 도구를 사용하세요. 사용자에게 식별자(ID)를 직접 다시 묻기 전에 반드시 이 추론 과정을 먼저 수행해야 합니다. 정확한 매칭이 어렵다면, 가능한 후보들을 제시하며 사용자에게 선택을 요청하거나, 정중하게 다시 문의할 수 있습니다.

        2.  **정보 수집 계획 (Tool Chaining):**
            *   사용자의 요청을 해결하기 위해 어떤 정보가 필요한지 판단합니다.
            *   필요한 정보를 얻기 위해 어떤 도구를 어떤 순서로 사용해야 할지 계획을 세웁니다.
            *   각 도구 사용 전에 어떤 정보를 얻기 위해 이 도구를 사용하는지 명확히 인지합니다.

        3.  **능동적 도구 사용 및 결과 분석:** 계획에 따라 도구를 사용하고, 그 결과를 분석하여 다음 단계를 진행합니다. 도구 사용 결과가 기대와 다르거나 정보가 부족하다면, 추가적인 도구 사용을 고려하거나 사용자에게 상황을 설명하고 대안을 제시하세요.

        4.  **결론 및 제안:** 수집된 정보와 분석 결과를 바탕으로 사용자에게 명확한 결론과 함께 다음 행동을 제안합니다.

        5.  **대화 흐름 및 사용자 경험:**
            *   항상 이전 대화 내용을 기억하고, 자연스럽게 대화를 이어가세요.
            *   **Tool 사용 비공개**: 사용자에게 응답할 때, 내부적으로 사용하는 Tool의 이름이나 식별자(ID)와 같은 구체적인 기술적 세부 정보를 직접 언급하지 마세요. 대신, Tool 사용의 목적이나 결과를 자연스러운 대화 형태로 전달하세요. (예: "관심 있는 지역을 말씀해주시면 자세히 알아볼게요." 또는 "팝업 스토어에 적합한 장소를 추천해 드릴까요?")
            *   사용자가 이전 답변에 대해 추가 질문을 하면 성실히 답변해주세요.

        **응답 형식:**
        - 사용자에게 친절하고 명확한 어투를 사용합니다.
        - 추천이나 분석 결과를 제시할 때는 그 근거를 함께 설명합니다.
        - 필요한 경우, 사용자에게 선택지를 제공하여 의사결정을 돕습니다.
        """

        val conversationHistory = context.chat.messages.joinToString("\n") {
            "${it.role}: ${it.content}"
        }

        val currentUserMessage = "${context.message?.role}: ${context.message?.content}"

        // 최종 프롬프트 구성
        return Prompt("""
        $systemInstructions

        **이전 대화 기록:**
        $conversationHistory

        **현재 사용자 요청:**
        $currentUserMessage

        **PopVision의 응답:**
        """.trimIndent())
    }
} 