package com.wheretopop.infrastructure.chat.prompt

import java.time.Instant

/**
 * ReAct strategy selection response parsing data classes for multi-step execution
 */
data class ReActResponse(
    val thought: String,
    val actions: List<ActionStep>,
    val observation: String
)

data class ActionStep(
    val step: Int,
    val strategy: String,
    val purpose: String,
    val reasoning: String,
    val recommended_tools: List<String>,
    val tool_sequence: String,
    val expected_output: String,
    val dependencies: List<Int> = emptyList() // 이전 단계 의존성
)

data class ExecutionPlan(
    val total_steps: Int,
    val execution_strategy: String,
    val final_goal: String
)

/**
 * 스트림 메시지 타입 정의
 */
enum class StreamMessageType {
    THINKING,           // 사고 과정 중 (planning)
    STEP_PLANNING,      // 단계 계획 중
    TOOL_EXECUTING,     // 도구 실행 중 (조회, 검색 등)
    DATA_PROCESSING,    // 데이터 처리 중
    RESPONSE_GENERATING,// 응답 생성 중
    RESPONSE_CHUNK,     // 실제 응답 글자별 스트림
    STATUS_UPDATE,      // 상태 업데이트
    COMPLETED,          // 완료
    ERROR              // 에러
}

/**
 * 통합 스트림 응답 모델
 */
data class ChatStreamResponse(
    val chatId: String,
    val executionId: String,
    val type: StreamMessageType,
    val timestamp: Instant = Instant.now(),
    
    // 사고 과정 관련
    val thinkingMessage: String? = null,
    
    // 상태 관련
    val currentStep: Int? = null,
    val totalSteps: Int = 0,
    val progress: Double = 0.0,
    val statusMessage: String? = null,
    
    // 도구 실행 관련
    val toolName: String? = null,
    val actionDescription: String? = null,
    
    // 응답 생성 관련
    val responseChunk: String? = null,
    val isComplete: Boolean = false,
    val finalResponse: String? = null,
    
    // 에러 관련
    val errorMessage: String? = null,
    val errorCode: String? = null
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
 * 실시간 사고 과정을 위한 데이터 클래스
 */
data class ThinkingProcess(
    val step: String,
    val description: String,
    val duration: Long = 0L // milliseconds
)

/**
 * 도구 실행 상태
 */
data class ToolExecutionStatus(
    val toolName: String,
    val action: String,
    val status: ToolStatus,
    val progressMessage: String,
    val estimatedTimeRemaining: Long? = null
)

enum class ToolStatus {
    STARTING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
} 