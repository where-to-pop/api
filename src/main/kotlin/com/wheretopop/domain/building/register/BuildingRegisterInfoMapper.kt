package com.wheretopop.domain.building.register

/**
 * 도메인 객체에서 DTO로 변환하는 매퍼 클래스
 */
class BuildingRegisterInfoMapper {

    fun of(domain: BuildingRegister): BuildingRegisterInfo.Main {
        return BuildingRegisterInfo.Main(
            id = domain.id.value,
            address = domain.address,
            location = BuildingRegisterInfo.LocationInfo(
                latitude = domain.location.latitude,
                longitude = domain.location.longitude
            ),
            heit = domain.heit,
            grndFlrCnt = domain.grndFlrCnt,
            ugrndFlrCnt = domain.ugrndFlrCnt,
            rideUseElvtCnt = domain.rideUseElvtCnt,
            emgenUseElvtCnt = domain.emgenUseElvtCnt,
            useAprDay = domain.useAprDay,
            bldNm = domain.bldNm,
            platArea = domain.platArea,
            archArea = domain.archArea,
            bcRat = domain.bcRat,
            valRat = domain.valRat,
            totArea = domain.totArea,
        )
    }

    /**
     * BuildingRegister 도메인 객체 목록을 BuildingRegisterInfo.Main DTO 목록으로 변환
     */
    fun of(domains: List<BuildingRegister>): List<BuildingRegisterInfo.Main> {
        return domains.map(::of)
    }

}