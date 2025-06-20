package com.wheretopop.infrastructure.chat.prompt

import java.time.Instant

/**
 * ReAct strategy selection generation parsing retrieval classes for multi-step execution
 */
data class ReActResponse(
    val thought: String,
    val actions: List<ActionStep>,
    val observation: String,
)

data class ReActExecutionInput (
    val reActResponse: ReActResponse,
    val requirementAnalysis: RequirementAnalysis
)

data class ActionStep(
    val step: Int,
    val strategy: String,
    val purpose: String,
    val reasoning: String,
    val expected_output: String,
    val dependencies: List<Int> = emptyList() // 이전 단계 의존성
)



/**
 * ReAct 실행 상태 및 스트림 응답을 위한 데이터 클래스들 (호환성 유지)
 */
data class ReActExecutionStatus(
    val chatId: String,
    val executionId: String,
    val phase: ExecutionPhase,
    val currentStep: Int?,
    val totalSteps: Int,
    val progress: Double, // 0.0 ~ 1.0
    val message: String,
    val stepResult: String? = null,
    val error: String? = null,
    val timestamp: Instant = Instant.now()
)

enum class ExecutionPhase {
    PLANNING,           // 실행 계획 생성 중
    STEP_EXECUTING,     // 개별 단계 실행 중
    STEP_COMPLETED,     // 개별 단계 완료
    STEP_FAILED,        // 개별 단계 실패
    AGGREGATING,        // 결과 통합 중
    COMPLETED,          // 전체 실행 완료
    FAILED              // 전체 실행 실패
}

data class ReActStreamResponse(
    val status: ReActExecutionStatus,
    val isComplete: Boolean = false,
    val finalResult: String? = null
)

/**
 * RAG 패턴을 위한 단계 분리 데이터 클래스
 */
data class RAGSteps(
    val retrievalAugmentationSteps: List<ActionStep>, // R+A 단계들 (배치 실행)
    val generationStep: ActionStep                    // G 단계 (스트리밍 실행)
)

/**
 * 요구사항 복잡도 레벨
 */
enum class ComplexityLevel {
    SIMPLE,     // 단순 - 일반 응답만으로 충분
    MODERATE,   // 보통 - 1-2개 데이터 소스 필요
    COMPLEX     // 복잡 - 다중 데이터 소스 및 분석 필요
}

/**
 * 요구사항 분석 결과
 */
data class RequirementAnalysis(
    val userIntent: String,           // 사용자 의도
    val processedQuery: String,       // 가공된 쿼리
    val complexityLevel: ComplexityLevel, // 복잡도
    val contextSummary: String,       // 컨텍스트 요약
    val reasoning: String             // 분석 근거
)

/**
 * 실행 계획 생성 결과
 */
sealed class ExecutionPlanningResult {
    data class Progress(val streamResponse: ReActStreamResponse) : ExecutionPlanningResult()
    data class Complete(val plan: ReActExecutionInput) : ExecutionPlanningResult()
}
