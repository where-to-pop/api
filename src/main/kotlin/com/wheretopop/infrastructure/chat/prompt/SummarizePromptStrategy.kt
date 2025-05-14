package com.wheretopop.infrastructure.chat.prompt

import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

/**
 * 대화 요약을 위한 프롬프트 전략
 */
@Component
class SummarizePromptStrategy : ChatPromptStrategy {
    private val requirements = setOf(
        PromptContextRequirement.CHAT,
        PromptContextRequirement.NO_USER_MESSAGE
    )

    override fun getRequirements(): Set<PromptContextRequirement> = requirements

    override fun canHandle(context: PromptContext): Boolean {
        return context.chat.messages.isNotEmpty() && 
               context.message == null
    }

    override suspend fun buildPrompt(context: PromptContext): Prompt {
        require(canHandle(context)) { "Context requirements not met for SummarizePromptStrategy" }

        return Prompt("""
            다음 대화 내용을 간단하게 요약해주세요.
            요약은 다음 조건을 만족해야 합니다:
            1. 핵심 주제나 목적을 포함할 것
            2. 50자 이내로 작성할 것
            3. 가장 중요한 결론이나 결정사항을 포함할 것
            
            대화 내용:
            ${context.chat.messages.joinToString("\n") { "${it.role}: ${it.content}" }}
        """.trimIndent())
    }
} 