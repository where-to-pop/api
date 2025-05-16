package com.wheretopop.shared.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.io.Serializable as JavaSerializable

/**
 * Snowflake ID 형식의 고유 식별자 클래스
 * 
 * 64비트 구성:
 * - sign bit (1비트): 항상 0 (양수 보장)
 * - timestamp (41비트): 밀리초 단위 타임스탬프 (약 69년)
 * - worker id (10비트): 서버/프로세스 식별자 (0-1023)
 * - sequence (12비트): 같은 밀리초 내 순차번호 (0-4095)
 */
@Serializable(with = UniqueIdSerializer::class)
open class UniqueId protected constructor(
    val value: Long
) : JavaSerializable {
    companion object {
        private const val serialVersionUID = 1L
        
        // 2024-01-01 00:00:00 UTC
        private const val EPOCH = 1704067200000L
        
        // 비트 시프트 상수
        private const val TIMESTAMP_BITS = 41
        private const val WORKER_ID_BITS = 10
        private const val SEQUENCE_BITS = 12
        
        private val MAX_WORKER_ID = (1 shl WORKER_ID_BITS) - 1
        private val MAX_SEQUENCE = (1 shl SEQUENCE_BITS) - 1
        
        // 비트 시프트 위치
        private const val TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS
        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        
        // 워커 ID (환경변수나 설정으로 주입받을 수 있도록 변경 가능)
        private val workerId = (System.getenv("WORKER_ID")?.toIntOrNull() ?: 0)
            .coerceIn(0, MAX_WORKER_ID)
        
        // 시퀀스 번호 (밀리초 단위로 리셋)
        private val sequence = AtomicInteger(0)
        private var lastTimestamp = -1L
        
        @Synchronized
        @JvmStatic
        fun create(): UniqueId {
            var timestamp = getCurrentTimestamp()
            
            // 이전 타임스탬프와 같으면 시퀀스 증가
            if (timestamp == lastTimestamp) {
                val seq = sequence.incrementAndGet()
                if (seq > MAX_SEQUENCE) {
                    // 시퀀스가 최대값을 초과하면 다음 밀리초까지 대기
                    do {
                        timestamp = getCurrentTimestamp()
                    } while (timestamp <= lastTimestamp)
                    sequence.set(0)
                }
            } else {
                sequence.set(0)
            }
            
            lastTimestamp = timestamp
            
            val id = ((timestamp - EPOCH).toLong() shl TIMESTAMP_SHIFT) or
                     (workerId.toLong() shl WORKER_ID_SHIFT) or
                     sequence.get().toLong()
            
            return UniqueId(id)
        }
        
        @JvmStatic
        fun of(value: Long): UniqueId {
            require(value >= 0) { "UniqueId value must be non-negative" }
            return UniqueId(value)
        }

        @JvmStatic
        fun of(value: String): UniqueId {
            return of(value.toLongOrNull() ?: throw IllegalArgumentException("Invalid UniqueId value"))
        }
        
        private fun getCurrentTimestamp(): Long {
            return Instant.now().toEpochMilli()
        }
    }
    
    /**
     * ID에서 타임스탬프 추출 (밀리초 단위)
     */
    fun getTimestamp(): Long {
        return ((value shr TIMESTAMP_SHIFT) + EPOCH)
    }
    
    /**
     * ID에서 워커 ID 추출
     */
    fun getWorkerId(): Int {
        return ((value shr WORKER_ID_SHIFT) and MAX_WORKER_ID.toLong()).toInt()
    }
    
    /**
     * ID에서 시퀀스 번호 추출
     */
    fun getSequence(): Int {
        return (value and MAX_SEQUENCE.toLong()).toInt()
    }
    
    fun toLong(): Long {
        return value
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as UniqueId
        
        return value == other.value
    }
    
    override fun hashCode(): Int {
        return value.hashCode()
    }
    
    override fun toString(): String {
        return value.toString()
    }
}

object UniqueIdSerializer : KSerializer<UniqueId> {
    override val descriptor = PrimitiveSerialDescriptor("UniqueId", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): UniqueId {
        return UniqueId.of(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: UniqueId) {
        encoder.encodeLong(value.toLong())
    }
} 