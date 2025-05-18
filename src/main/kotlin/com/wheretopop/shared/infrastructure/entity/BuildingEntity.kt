package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.BuildingIdConverter
import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.shared.model.Location
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant

/**
 * 빌딩(Building) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "buildings")
@EntityListeners(AuditingEntityListener::class)
class BuildingEntity(
    @Id
    @JdbcTypeCode(Types.BIGINT)

    @Convert(converter = BuildingIdConverter::class)
    val id: BuildingId,
    
    @Column(nullable = false)
    val address: String,
    
    @Column(nullable = false)
    val latitude: Double,
    
    @Column(nullable = false)
    val longitude: Double,
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
    
    @Column(name = "deleted_at")
    val deletedAt: Instant? = null
) {
    companion object {
        fun of(building: Building): BuildingEntity {
            return BuildingEntity(
                id = building.id,
                address = building.address,
                latitude = building.location.latitude,
                longitude = building.location.longitude,
                createdAt = building.createdAt,
                updatedAt = building.updatedAt,
                deletedAt = building.deletedAt
            )
        }
    }

    fun toDomain(): Building {
        return Building.create(
            id = id,
            address = address,
            location = Location(latitude, longitude),
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
    }
}
