package com.wheretopop.infrastructure.area

import com.wheretopop.shared.converter.UniqueIdConverter
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "areas",
    indexes = [Index(name = "idx_area_region_id", columnList = "region_id")]
)
@Comment("권역 정보 테이블")
class AreaEntity
    (
    @Id
    @Column(name = "id", nullable = false)
    @Convert(converter = UniqueIdConverter::class)
    @Comment("권역 고유 식별자 (Snowflake ID)") var id: UniqueId = UniqueId.create(), @Column(name = "name", nullable = false)
    @Comment("권역 이름") var name: String = "", @Column(name = "description")
    @Comment("권역 설명") var description: String? = null, @Column(name = "latitude")
    @Comment("위도") var latitude: Double? = null, @Column(name = "longitude")
    @Comment("경도") var longitude: Double? = null, @Column(name = "region_id")
    @Comment("지역 ID (FK - regions 테이블)(논리적 fk)") var regionId: Long? = null
) : AbstractEntity() {


}
