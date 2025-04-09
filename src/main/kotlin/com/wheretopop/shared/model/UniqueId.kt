package com.wheretopop.shared.model

import java.io.Serializable
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

/**
 * 고유 식별자를 위한 Snowflake 알고리즘 기반 클래스
 * Twitter의 Snowflake ID 생성 방식을 기반으로 함
 * 
 * 64비트 ID 구조:
 * - 41비트: 타임스탬프 (밀리초)
 * - 10비트: 노드 ID (서버/프로세스 구분)
 * - 12비트: 시퀀스 번호 (같은 밀리초 내에서 생성된 ID 구분)
 */
class UniqueId private constructor(val value: Long) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
        
        private const val EPOCH = 1672531200000L // 2023-01-01T00:00:00Z

        private const val TIMESTAMP_BITS = 41
        private const val NODE_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private val MAX_NODE_ID = (1L shl NODE_ID_BITS) - 1
        private val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1

        private const val TIMESTAMP_SHIFT = NODE_ID_BITS + SEQUENCE_BITS
        private const val NODE_ID_SHIFT = SEQUENCE_BITS

        // 기본 노드 ID (필요시 환경변수나 설정에서 주입)
        private val nodeId = System.getProperty("nodeId")?.toLongOrNull() ?: 1L

        // 시퀀스 번호
        private val sequenceGenerator = AtomicLong(0L)
        private var lastTimestamp = -1L

        init {
            require(nodeId in 0..MAX_NODE_ID) { "노드 ID는 0에서 $MAX_NODE_ID 사이여야 합니다." }
        }

        /**
         * 새로운 고유 ID 생성
         */
        @Synchronized
        fun create(): UniqueId {
            var timestamp = getCurrentTimestamp()

            // 같은 밀리초 내에서는 시퀀스 증가
            if (timestamp == lastTimestamp) {
                val sequence = (sequenceGenerator.incrementAndGet() and MAX_SEQUENCE)
                // 시퀀스 값이 최대값에 도달하면 다음 밀리초까지 대기
                if (sequence == 0L) {
                    timestamp = waitNextMillis(lastTimestamp)
                }
            } else {
                sequenceGenerator.set(0L)
            }

            lastTimestamp = timestamp

            val id = ((timestamp - EPOCH) shl TIMESTAMP_SHIFT) or
                    (nodeId shl NODE_ID_SHIFT) or
                    sequenceGenerator.get()

            return UniqueId(id)
        }

        /**
         * 기존 ID 값으로부터 UniqueId 객체 생성
         */
        fun of(id: Long): UniqueId {
            return UniqueId(id)
        }

        /**
         * 현재 타임스탬프 취득 (밀리초)
         */
        private fun getCurrentTimestamp(): Long {
            return Instant.now().toEpochMilli()
        }

        /**
         * 다음 밀리초까지 대기
         */
        private fun waitNextMillis(lastTimestamp: Long): Long {
            var timestamp = getCurrentTimestamp()
            while (timestamp <= lastTimestamp) {
                timestamp = getCurrentTimestamp()
            }
            return timestamp
        }
    }

    /**
     * ID에서 타임스탬프 부분 추출
     */
    fun getTimestamp(): Long {
        return ((value shr TIMESTAMP_SHIFT) + EPOCH)
    }

    /**
     * ID에서 노드 ID 부분 추출
     */
    fun getNodeId(): Long {
        return (value shr NODE_ID_SHIFT) and MAX_NODE_ID
    }

    /**
     * ID에서 시퀀스 부분 추출
     */
    fun getSequence(): Long {
        return value and MAX_SEQUENCE
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UniqueId) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }
} 