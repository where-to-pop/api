package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.model.Location
import com.wheretopop.shared.model.UniqueId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("areas")
data class AreaEntity(
    @Id
    @Column("id")
    val id: Long,
    @Column("name")
    val name: String,
    @Column("description")
    val description: String,
    @Column("latitude")
    val latitude: Double,
    @Column("longitude")
    val longitude: Double,
    @Column("region_id")
    val regionId: Long?,
    @Column("created_at")
    val createdAt: LocalDateTime,
    @Column("updated_at")
    val updatedAt: LocalDateTime,
    @Column("deleted_at")
    val deletedAt: LocalDateTime?
) {
    companion object {
        fun of(area: Area): AreaEntity {
            return AreaEntity(
                id = area.id.toLong(),
                name = area.name,
                description = area.description ?: "",
                latitude = area.location.latitude ?: 0.0,
                longitude = area.location.longitude ?: 0.0,
                regionId = area.regionId,
                createdAt = area.createdAt,
                updatedAt = area.updatedAt,
                deletedAt = area.deletedAt
            )
        }
    }

    fun toDomain(): Area {
        return Area.create(
            id = UniqueId.of(id),
            name = name,
            description = description,
            location = Location(latitude, longitude),
            regionId = regionId
        )
    }

    fun update(area: Area): AreaEntity {
        return copy(
            name = area.name,
            description = area.description ?: "",
            latitude = area.location.latitude ?: 0.0,
            longitude = area.location.longitude ?: 0.0,
            regionId = area.regionId,
            updatedAt = LocalDateTime.now()
        )
    }
}
