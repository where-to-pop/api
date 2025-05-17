package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.JpaConverterConfig
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.shared.model.Location
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 빌딩 등록정보(BuildingRegister) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "building_registers")
@EntityListeners(AuditingEntityListener::class)
class BuildingRegisterEntity(
    @Id
    @Convert(converter = JpaConverterConfig.BuildingRegisterIdConverter::class)
    val id: BuildingRegisterId,
    
    @Column(name = "building_id", nullable = false)
    @Convert(converter = JpaConverterConfig.BuildingIdConverter::class)
    val buildingId: BuildingId,
    
    @Column(nullable = false)
    val address: String,
    
    @Column
    val heit: Double? = null,
    
    @Column(name = "grnd_flr_cnt")
    val grndFlrCnt: Int? = null,
    
    @Column(name = "ugrnd_flr_cnt")
    val ugrndFlrCnt: Int? = null,
    
    @Column(name = "ride_use_elvt_cnt")
    val rideUseElvtCnt: Int? = null,
    
    @Column(name = "emgen_use_elvt_cnt")
    val emgenUseElvtCnt: Int? = null,
    
    @Column(name = "use_apr_day")
    val useAprDay: Instant? = null,
    
    @Column(name = "bld_nm")
    val bldNm: String? = null,
    
    @Column(name = "plat_area")
    val platArea: Double? = null,
    
    @Column(name = "arch_area")
    val archArea: Double? = null,
    
    @Column(name = "bc_rat")
    val bcRat: Double? = null,
    
    @Column(name = "val_rat")
    val valRat: Double? = null,
    
    @Column(name = "tot_area")
    val totArea: Double? = null,
    
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
            deletedAt = deletedAt
        )
    }
}

