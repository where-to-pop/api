package com.wheretopop.shared.model

import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.Serializable as JavaSerializable

/**
 * 고유 식별자를 위한 클래스
 * uuid-creator 라이브러리의 시간 기반 UUID 알고리즘을 사용
 */
@Serializable(with = UniqueIdSerializer::class)
open class UniqueId protected constructor(
    val value: Long
) : JavaSerializable {
    companion object {
        private const val serialVersionUID = 1L
        
        @JvmStatic
        fun create(): UniqueId {
            // 시간 기반 UUID 생성
            val uuid = UuidCreator.getTimeOrderedEpoch()
            
            // 최상위 비트(부호 비트)를 0으로 설정하여 항상 양수 보장
            val positiveValue = uuid.leastSignificantBits and Long.MAX_VALUE
            
            return UniqueId(positiveValue)
        }
        
        @JvmStatic
        fun of(value: Long): UniqueId {
            // 입력 값이 음수인 경우 양수로 변환
            val positiveValue = if (value < 0) value and Long.MAX_VALUE else value
            return UniqueId(positiveValue)
        }
    }
    
    fun getTimestamp(): Long {
        return value shr 16
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