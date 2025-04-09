package com.wheretopop.shared.converter

import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

/**
 * UniqueId와 Long 간의 변환을 처리하는 JPA Converter
 * 이 클래스는 엔티티의 UniqueId 타입 필드를 DB의 Long 타입으로 자동 변환해줍니다.
 */
@Component
@Converter(autoApply = true)
class UniqueIdConverter : AttributeConverter<UniqueId, Long> {
    
    /**
     * UniqueId를 DB에 저장하기 위한 Long 값으로 변환
     */
    override fun convertToDatabaseColumn(attribute: UniqueId?): Long? {
        return attribute?.value
    }

    /**
     * DB의 Long 값을 UniqueId 객체로 변환
     */
    override fun convertToEntityAttribute(dbData: Long?): UniqueId? {
        return dbData?.let { UniqueId.of(it) }
    }
} 