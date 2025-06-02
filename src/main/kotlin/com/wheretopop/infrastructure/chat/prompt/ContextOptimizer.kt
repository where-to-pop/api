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
    
    /**
     * 단계별 실행을 위한 프롬프트를 생성합니다.
     */
    fun buildStepPrompt(step: ActionStep, accumulatedContext: String, dependencyResults: String): String {
        return """
            You are executing Step ${step.step} of a multi-step plan.
            
            Step Purpose: ${step.purpose}
            Step Strategy: ${step.strategy}
            Expected Output: ${step.expected_output}
            
            Context from previous steps:
            $accumulatedContext
            
            ${if (dependencyResults.isNotBlank()) "Dependency results:\n$dependencyResults\n" else ""}
            
            Please execute this step according to your strategy guidelines and produce the expected output.
            Focus specifically on: ${step.purpose}
        """.trimIndent()
    }
    
    /**
     * 컨텍스트 크기를 최적화합니다 (메모리 효율성을 위해)
     */
    fun optimizeContextSize(context: String, maxLength: Int = 2000): String {
        return if (context.length > maxLength) {
            val truncated = context.take(maxLength - 3) + "..."
            logger.warn("Context truncated from ${context.length} to ${truncated.length} characters")
            truncated
        } else {
            context
        }
    }
    
    /**
     * 전략 타입에 따라 컨텍스트를 필터링합니다.
     */
    fun filterContextByStrategyType(context: String, strategyType: String): String {
        return when (strategyType.lowercase()) {
            "area_query" -> filterForAreaQuery(context)
            "building_query" -> filterForBuildingQuery(context)
            "popup_query" -> filterForPopupQuery(context)
            else -> context
        }
    }
    
    private fun filterForAreaQuery(context: String): String {
        // 지역 관련 키워드만 남기고 불필요한 내용 필터링
        val areaKeywords = listOf("지역", "역", "구", "동", "거리", "위치", "area", "location")
        return context.lines().filter { line ->
            areaKeywords.any { keyword -> line.contains(keyword, ignoreCase = true) }
        }.joinToString("\n").ifEmpty { context }
    }
    
    private fun filterForBuildingQuery(context: String): String {
        // 건물 관련 키워드만 남기고 불필요한 내용 필터링
        val buildingKeywords = listOf("건물", "빌딩", "매장", "층", "규모", "building", "store", "floor")
        return context.lines().filter { line ->
            buildingKeywords.any { keyword -> line.contains(keyword, ignoreCase = true) }
        }.joinToString("\n").ifEmpty { context }
    }
    
    private fun filterForPopupQuery(context: String): String {
        // 팝업 관련 키워드만 남기고 불필요한 내용 필터링
        val popupKeywords = listOf("팝업", "popup", "브랜드", "이벤트", "기간", "콘셉트", "brand", "event")
        return context.lines().filter { line ->
            popupKeywords.any { keyword -> line.contains(keyword, ignoreCase = true) }
        }.joinToString("\n").ifEmpty { context }
    }
} 