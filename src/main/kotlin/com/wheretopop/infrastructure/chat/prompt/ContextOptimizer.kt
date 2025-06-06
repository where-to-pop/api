package com.wheretopop.infrastructure.chat.prompt

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * 컨텍스트 최적화를 담당하는 클래스
 */
@Component
class ContextOptimizer {
    private val logger = KotlinLogging.logger {}
    
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

} 