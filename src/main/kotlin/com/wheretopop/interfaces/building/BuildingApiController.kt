package com.wheretopop.interfaces.building

import com.wheretopop.shared.response.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.wheretopop.shared.response.ErrorCode
@Tag(name = "빌딩(Building)", description = "빌딩(Building)에 대한 API")
@RestController
@RequestMapping("/api/v1/buildings")
class BuildingApiController {
    // private val buildingDtoMapper = BuildingDtoMapper()

    @GetMapping()
    @Operation(summary = "필터를 이용하여 빌딩 정보를 조회합니다.")
    fun searchBuildings(@ParameterObject request: BuildingDto.SearchRequest): CommonResponse<List<BuildingDto.BuildingResponse>> {
        // val criteria = buildingDtoMapper.toCriteria(request)
        // val domainResults = buildingFacade.searchBuildings(criteria)
        // val response = buildingDtoMapper.toBuildingResponses(domainResults)

        return CommonResponse.success(emptyList())
    }

    @GetMapping("/{buildingToken}")
    @Operation(summary = "빌딩 상세 정보를 조회합니다.")
    fun getBuilding(
        @Parameter(description = "빌딩 대체키")
        @PathVariable buildingToken: String
    ): CommonResponse<BuildingDto.BuildingResponse> {
        // val building = buildingFacade.getBuilding(buildingToken)
        // val response = buildingDtoMapper.toBuildingResponse(building)
        TODO("빌딩 상세 정보를 조회합니다.")
    }
} 