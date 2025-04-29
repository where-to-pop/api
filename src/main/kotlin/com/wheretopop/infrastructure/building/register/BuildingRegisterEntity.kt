package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.shared.model.Location
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("building_register")
internal class BuildingRegisterEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: BuildingRegisterId,
    @Column("address")
    val address: String,
    @Column("latitude")
    val latitude: Double,
    @Column("longitude")
    val longitude: Double,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
) {
    companion object {
        fun of(buildingRegister: BuildingRegister): BuildingRegisterEntity {
            return BuildingRegisterEntity(
                id = buildingRegister.id,
                address = buildingRegister.address,
                latitude = buildingRegister.location.latitude,
                longitude = buildingRegister.location.longitude,
                createdAt = buildingRegister.createdAt,
                updatedAt = buildingRegister.updatedAt,
                deletedAt = buildingRegister.deletedAt
            )
        }
    }

    fun toDomain(): BuildingRegister {
        return BuildingRegister.create(
            id = id,
            address = address,
            location = Location(latitude, longitude),
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
    }

    fun update(buildingRegister: BuildingRegister): BuildingRegisterEntity {
        return BuildingRegisterEntity(
            id = id,
            address = buildingRegister.address,
            latitude = buildingRegister.location.latitude,
            longitude = buildingRegister.location.longitude,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}

@WritingConverter
class BuildingRegisterIdToLongConverter : Converter<BuildingRegisterId, Long> {
    override fun convert(source: BuildingRegisterId) = source.toLong()
}


@ReadingConverter
class LongToBuildingRegisterIdConverter : Converter<Long, BuildingRegisterId> {
    override fun convert(source: Long) = BuildingRegisterId.of(source)
}

