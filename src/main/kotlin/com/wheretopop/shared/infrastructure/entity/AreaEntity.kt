package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.JpaConverterConfig
import com.wheretopop.domain.area.Area
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.shared.model.Location
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 지역(Area) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "areas")
@EntityListeners(AuditingEntityListener::class)
class AreaEntity(
    @Id
    @Convert(converter = JpaConverterConfig.AreaIdConverter::class)
    val id: AreaId,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false)
    var latitude: Double,

    @Column(nullable = false)
    var longitude: Double,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
) {
    fun toDomain(): Area {
        return Area.create(
            id = id,
            name = name,
            description = description,
            location = Location(latitude, longitude),
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
    }

    companion object {
        fun from(area: Area): AreaEntity {
            return AreaEntity(
                id = area.id,
                name = area.name,
                description = area.description,
                latitude = area.location.latitude,
                longitude = area.location.longitude,
                createdAt = area.createdAt,
                updatedAt = area.updatedAt,
                deletedAt = area.deletedAt
            )
        }
    }
}

