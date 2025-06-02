package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyExecutionType
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import mu.KotlinLogging
import org.springframework.stereotype.Component

/**
 * 실행 계획 최적화를 담당하는 클래스
 */
@Component
class ExecutionPlanOptimizer {
    private val logger = KotlinLogging.logger {}
    
    /**
     * 전략 타입별로 체계적인 실행 순서를 관리합니다.
     */
    fun optimizeExecutionPlan(executionPlan: ReActResponse): ReActResponse {
        val optimizedActions = executionPlan.actions.sortedWith(compareBy<ActionStep> { 
            when (StrategyType.findById(it.strategy)?.executionType) {
                StrategyExecutionType.DATA_COLLECTION -> 1
                StrategyExecutionType.DATA_PROCESSING -> 2
                StrategyExecutionType.DECISION_MAKING -> 3
                StrategyExecutionType.RESPONSE_GENERATION -> 4
                null -> 5
            }
        }.thenBy { it.step })
        
        // 단계 번호 재정렬
        val reorderedActions = optimizedActions.mapIndexed { index, action ->
            action.copy(step = index + 1)
        }
        
        logger.info("Optimized execution plan by strategy types:")
        reorderedActions.forEach { action ->
            val strategyType = StrategyType.findById(action.strategy)
            logger.info("  Step ${action.step}: ${action.strategy} (${strategyType?.executionType})")
        }
        
        return executionPlan.copy(actions = reorderedActions)
    }
    
    /**
     * 전략 타입별 병렬 실행 가능성을 판단합니다.
     */
    fun canExecuteInParallel(action1: ActionStep, action2: ActionStep): Boolean {
        val type1 = StrategyType.findById(action1.strategy)?.executionType
        val type2 = StrategyType.findById(action2.strategy)?.executionType
        
        // 같은 타입이고 의존성이 없으면 병렬 실행 가능
        return type1 == type2 && 
               action1.dependencies.none { it == action2.step } &&
               action2.dependencies.none { it == action1.step }
    }
    
    /**
     * 실행 계획의 의존성을 검증합니다.
     */
    fun validateDependencies(executionPlan: ReActResponse): Boolean {
        val stepNumbers = executionPlan.actions.map { it.step }.toSet()
        
        for (action in executionPlan.actions) {
            for (dependency in action.dependencies) {
                if (dependency !in stepNumbers) {
                    logger.error("Invalid dependency: Step ${action.step} depends on non-existent step $dependency")
                    return false
                }
                if (dependency >= action.step) {
                    logger.error("Circular dependency: Step ${action.step} depends on future step $dependency")
                    return false
                }
            }
        }
        
        return true
    }
    
    /**
     * 실행 계획을 병렬 처리에 최적화된 배치로 그룹화합니다.
     */
    fun groupIntoExecutionBatches(actions: List<ActionStep>): List<List<ActionStep>> {
        val batches = mutableListOf<List<ActionStep>>()
        val remaining = actions.toMutableList()
        val completed = mutableSetOf<Int>()
        
        while (remaining.isNotEmpty()) {
            // 현재 실행 가능한 단계들 찾기 (의존성이 모두 완료된 단계들)
            val readyActions = remaining.filter { action ->
                action.dependencies.all { it in completed }
            }
            
            if (readyActions.isEmpty()) {
                // 데드락 상황 - 남은 단계를 강제로 배치에 추가
                logger.warn("Potential deadlock detected, adding remaining steps to batch")
                batches.add(remaining.toList())
                break
            }
            
            // 병렬 실행 가능한 단계들을 배치로 그룹화
            val parallelBatch = findParallelExecutableActions(readyActions)
            batches.add(parallelBatch)
            
            // 완료된 단계들 추가
            parallelBatch.forEach { completed.add(it.step) }
            remaining.removeAll(parallelBatch)
        }
        
        logger.info("Grouped ${actions.size} actions into ${batches.size} execution batches")
        batches.forEachIndexed { index, batch ->
            logger.info("  Batch ${index + 1}: ${batch.map { "Step ${it.step}(${it.strategy})" }}")
        }
        
        return batches
    }
    
    /**
     * 병렬 실행 가능한 액션들을 찾습니다.
     */
    private fun findParallelExecutableActions(readyActions: List<ActionStep>): List<ActionStep> {
        if (readyActions.isEmpty()) return emptyList()
        
        val parallelGroup = mutableListOf<ActionStep>()
        parallelGroup.add(readyActions.first())
        
        for (i in 1 until readyActions.size) {
            val candidate = readyActions[i]
            // 현재 그룹의 모든 액션과 병렬 실행 가능한지 확인
            if (parallelGroup.all { canExecuteInParallel(it, candidate) }) {
                parallelGroup.add(candidate)
            }
        }
        
        return parallelGroup
    }
    
    /**
     * 실행 계획의 예상 소요 시간을 계산합니다.
     */
    fun estimateExecutionTime(executionPlan: ReActResponse): Long {
        val batches = groupIntoExecutionBatches(executionPlan.actions)
        
        // 각 전략 타입별 예상 소요 시간 (밀리초)
        val strategyExecutionTimes = mapOf(
            StrategyExecutionType.DATA_COLLECTION to 3000L,
            StrategyExecutionType.DATA_PROCESSING to 2000L,
            StrategyExecutionType.DECISION_MAKING to 1500L,
            StrategyExecutionType.RESPONSE_GENERATION to 2500L
        )
        
        // 각 배치의 최대 소요 시간의 합
        val totalTime = batches.sumOf { batch ->
            batch.maxOfOrNull { action ->
                val strategyType = StrategyType.findById(action.strategy)
                strategyExecutionTimes[strategyType?.executionType] ?: 2000L
            } ?: 2000L
        }
        
        logger.info("Estimated execution time: ${totalTime}ms for ${batches.size} batches")
        return totalTime
    }
} 