package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.BuildingId
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
    @Column("building_id")
    val buildingId: BuildingId,
    @Column("address")
    val address: String,
    @Column("heit")
    val heit: Double? = null,
    @Column("grnd_flr_cnt")
    val grndFlrCnt: Int? = null,
    @Column("ugrnd_flr_cnt")
    val ugrndFlrCnt: Int? = null,
    @Column("ride_use_elvt_cnt")
    val rideUseElvtCnt: Int? = null,
    @Column("emgen_use_elvt_cnt")
    val emgenUseElvtCnt: Int? = null,
    @Column("use_apr_day")
    val useAprDay: Instant? = null,
    @Column("bld_nm")
    val bldNm: String? = null,
    @Column("plat_area")
    val platArea: Double? = null,
    @Column("arch_area")
    val archArea: Double? = null,
    @Column("bc_rat")
    val bcRat: Double? = null,
    @Column("val_rat")
    val valRat: Double? = null,
    @Column("tot_area")
    val totArea: Double? = null,
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
                buildingId = buildingRegister.buildingId,
                address = buildingRegister.address,
                latitude = buildingRegister.location.latitude,
                longitude = buildingRegister.location.longitude,
                heit = buildingRegister.heit,
                grndFlrCnt = buildingRegister.grndFlrCnt,
                ugrndFlrCnt = buildingRegister.ugrndFlrCnt,
                rideUseElvtCnt = buildingRegister.rideUseElvtCnt,
                emgenUseElvtCnt = buildingRegister.emgenUseElvtCnt,
                useAprDay = buildingRegister.useAprDay,
                bldNm = buildingRegister.bldNm,
                platArea = buildingRegister.platArea,
                archArea = buildingRegister.archArea,
                bcRat = buildingRegister.bcRat,
                valRat = buildingRegister.valRat,
                totArea = buildingRegister.totArea,
                createdAt = buildingRegister.createdAt,
                updatedAt = buildingRegister.updatedAt,
                deletedAt = buildingRegister.deletedAt
            )
        }
    }

    fun toDomain(): BuildingRegister {
        return BuildingRegister.create(
            id = id,
            buildingId = buildingId,
            address = address,
            location = Location(latitude, longitude),
            heit = heit,
            grndFlrCnt = grndFlrCnt,
            ugrndFlrCnt = ugrndFlrCnt,
            rideUseElvtCnt = rideUseElvtCnt,
            emgenUseElvtCnt = emgenUseElvtCnt,
            useAprDay = useAprDay,
            bldNm = bldNm,
            platArea = platArea,
            archArea = archArea,
            bcRat = bcRat,
            valRat = valRat,
            totArea = totArea,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
    }

    fun update(buildingRegister: BuildingRegister): BuildingRegisterEntity {
        return BuildingRegisterEntity(
            id = id,
            buildingId = buildingId,
            address = buildingRegister.address,
            latitude = buildingRegister.location.latitude,
            longitude = buildingRegister.location.longitude,
            heit = buildingRegister.heit,
            grndFlrCnt = buildingRegister.grndFlrCnt,
            ugrndFlrCnt = buildingRegister.ugrndFlrCnt,
            rideUseElvtCnt = buildingRegister.rideUseElvtCnt,
            emgenUseElvtCnt = buildingRegister.emgenUseElvtCnt,
            useAprDay = buildingRegister.useAprDay,
            bldNm = buildingRegister.bldNm,
            platArea = buildingRegister.platArea,
            archArea = buildingRegister.archArea,
            bcRat = buildingRegister.bcRat,
            valRat = buildingRegister.valRat,
            totArea = buildingRegister.totArea,
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

