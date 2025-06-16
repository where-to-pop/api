package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterCommand
import com.wheretopop.domain.building.register.BuildingRegisterStore
import com.wheretopop.infrastructure.building.register.external.vworld.areacode.AddressToAreaCodeApiCaller
import com.wheretopop.infrastructure.building.register.external.dataportal.register.KoreaBuildingRegisterApiCaller
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Instant

private val logger = KotlinLogging.logger {}


/**
 * BuildingRegisterStore 인터페이스 구현체
 * 도메인 레이어와 인프라 레이어를 연결하는 역할을 담당
 */
@Component
class BuildingRegisterStoreImpl(
    private val buildingRegisterRepository: BuildingRegisterRepository,
    private val addressToAreaCodeApiCaller: AddressToAreaCodeApiCaller,
    private val koreaBuildingRegisterApiCaller: KoreaBuildingRegisterApiCaller
) : BuildingRegisterStore {
    override fun callAndSave(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegister? {
        val buildingRegister = this.call(command) ?: return null
        return buildingRegisterRepository.save(buildingRegister)
    }
    override fun callAndSave(commands: List<BuildingRegisterCommand.CreateBuildingRegisterCommand>): List<BuildingRegister> {
        val buildingRegisters: List<BuildingRegister> = commands.mapNotNull { command ->
            this.call(command)
        }
        return buildingRegisterRepository.save(buildingRegisters)
    }

    private fun call(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegister? {
        try {
            val areaCode = addressToAreaCodeApiCaller.fetchAreaCode(command.address) ?: throw Exception("지역 코드를 불러오지 못했습니다.")
            val koreaBuildingRegisterResponse = koreaBuildingRegisterApiCaller.fetchBuildingRegisterData(
                sigunguCd = areaCode.sigunguCd,
                bjdongCd = areaCode.bjdongCd,
            )
            val firstKoreaBuildingRegister = koreaBuildingRegisterResponse?.response?.body?.items?.item?.get(0) ?: throw Exception("건축물 대장 정보를 불러오지 못했습니다.")

            val buildingRegister = BuildingRegister.create(
                buildingId = command.buildingId,
                address = command.address,
                location = command.location,
                heit = firstKoreaBuildingRegister.heit,
                grndFlrCnt = firstKoreaBuildingRegister.grndFlrCnt,
                ugrndFlrCnt = firstKoreaBuildingRegister.ugrndFlrCnt,
                rideUseElvtCnt = firstKoreaBuildingRegister.rideUseElvtCnt,
                emgenUseElvtCnt = firstKoreaBuildingRegister.emgenUseElvtCnt,
                useAprDay = firstKoreaBuildingRegister.useAprDay,
                bldNm = firstKoreaBuildingRegister.bldNm,
                platArea = firstKoreaBuildingRegister.platArea,
                archArea = firstKoreaBuildingRegister.archArea,
                bcRat = firstKoreaBuildingRegister.bcRat,
                valRat = firstKoreaBuildingRegister.vlRat,
                totArea = firstKoreaBuildingRegister.totArea,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null,
            )

            return buildingRegister
        } catch (e: Exception) {
            logger.error(e) { "Error creating building register for buildingId ${command.buildingId}" }
            return null
        }
    }
} 