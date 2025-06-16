package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * 컨텍스트 최적화를 담당하는 클래스
 */
@Component
class ContextOptimizer {
    private val logger = KotlinLogging.logger {}

    companion object {
        /**
         * 컨텍스트 최적화에 사용할 최근 메시지 개수
         */
        private const val CONTEXT_MESSAGE_COUNT = 5
    }
    
    /**
     * 최적화된 컨텍스트를 생성합니다 (필요한 정보만 포함)
     */
    fun buildOptimizedContext(
        originalUserMessage: String, 
        currentStep: ActionStep, 
        stepResults: ConcurrentHashMap<Int, String>
    ): String {
        val relevantResults = currentStep.dependencies.mapNotNull { depStep ->
            stepResults[depStep]?.let { "Step $depStep result: $it" }
        }
        
        return if (relevantResults.isNotEmpty()) {
            "Original query: $originalUserMessage\n\nRelevant previous results:\n${relevantResults.joinToString("\n")}"
        } else {
            "Original query: $originalUserMessage"
        }
    }
    
    /**
     * Chat 객체에서 컨텍스트를 포함한 최적화된 컨텍스트를 생성합니다
     */
    fun buildOptimizedContextWithChat(
        chat: Chat,
        requirementAnalysis: RequirementAnalysis?,
        currentStep: ActionStep,
        stepResults: ConcurrentHashMap<Int, String>
    ): String {
        val recentContext = chat.getRecentMessagesAsContext(CONTEXT_MESSAGE_COUNT)
        val latestUserMessage = chat.getLatestUserMessage()?.content ?: ""
        
        val relevantResults = currentStep.dependencies.mapNotNull { depStep ->
            stepResults[depStep]?.let { "Step $depStep result: $it" }
        }
        
        val contextParts = mutableListOf<String>()
        
        if (recentContext.isNotBlank()) {
            contextParts.add("Recent conversation context:\n$recentContext")
        }
        
        contextParts.add("## Current query: \n$latestUserMessage")
        
        if (relevantResults.isNotEmpty()) {
            contextParts.add("Relevant previous results:\n${relevantResults.joinToString("\n")}")
        }

        if (requirementAnalysis !== null) {
        contextParts.add(
            """
                ###Requirement Analysis:
                contextSummary: ${requirementAnalysis.contextSummary}
                userIntent: ${requirementAnalysis.userIntent}
                processedQuery: ${requirementAnalysis.processedQuery}
                \n\n
            """.trimMargin())
            }
        return contextParts.joinToString("\n\n")
    }

} 