package com.wheretopop.infrastructure.chat.prompt

import mu.KotlinLogging
import org.springframework.stereotype.Component

/**
 * 성능 모니터링을 담당하는 클래스
 */
@Component
class PerformanceMonitor {
    private val logger = KotlinLogging.logger {}
    
    /**
     * 작업의 실행 시간을 측정하고 로깅합니다.
     */
    fun <T> measureTimeSync(operation: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logger.info("$operation completed in ${duration}ms")
        }
    }


    /**
     * suspend 함수: suspend 블록의 실행 시간 측정
     */
    suspend fun <T> measureTimeAsync(operation: String, block: suspend () -> T): T {
        val startTime = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logger.info("$operation completed in ${duration}ms")
        }
    }
    
    /**
     * 작업 실행 시간을 측정하고 시간과 함께 결과를 반환합니다.
     */
    fun <T> measureTimeWithResult(operation: String, block: () -> T): Pair<T, Long> {
        val startTime = System.currentTimeMillis()
        val result = block()
        val duration = System.currentTimeMillis() - startTime
        logger.info("$operation completed in ${duration}ms")
        return result to duration
    }
    
    /**
     * 복수의 작업들의 실행 시간을 개별적으로 측정합니다.
     */
    fun measureBatchOperations(operations: Map<String, () -> Unit>) {
        val results = mutableMapOf<String, Long>()
        val totalStartTime = System.currentTimeMillis()
        
        operations.forEach { (name, operation) ->
            val startTime = System.currentTimeMillis()
            operation()
            val duration = System.currentTimeMillis() - startTime
            results[name] = duration
            logger.info("Batch operation '$name' completed in ${duration}ms")
        }
        
        val totalDuration = System.currentTimeMillis() - totalStartTime
        logger.info("All batch operations completed in ${totalDuration}ms: $results")
    }
    
    /**
     * 메모리 사용량을 로깅합니다.
     */
    fun logMemoryUsage(context: String) {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory() / 1024 / 1024 // MB
        val freeMemory = runtime.freeMemory() / 1024 / 1024 // MB
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory() / 1024 / 1024 // MB
        
        logger.info("Memory usage [$context]: Used ${usedMemory}MB / Total ${totalMemory}MB / Max ${maxMemory}MB")
    }
} 