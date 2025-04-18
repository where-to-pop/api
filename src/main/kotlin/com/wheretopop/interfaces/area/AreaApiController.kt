package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.wheretopop.shared.response.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "권역(Area)", description = "권역(Area)에 대한 API")
@RequestMapping("/v1/areas")
class AreaApiController(
    private val areaFacade: AreaFacade,
) {
    private val areaDtoMapper = AreaDtoMapper()

    @GetMapping()
    @Operation(summary = "필터를 이용하여 권역 정보를 조회합니다.")
    fun searchAreas(@ParameterObject request: AreaDto.SearchRequest): CommonResponse<List<AreaDto.AreaResponse>> {
        val criteria = areaDtoMapper.toCriteria(request)
        val domainResults = areaFacade.searchAreas(criteria)
        val response = areaDtoMapper.toAreaResponses(domainResults)

        return CommonResponse.success(response)
    }
    
    @PostMapping("/initialize")
    @Operation(summary = "기본 권역 데이터를 초기화합니다.", description = "기본 권역 데이터를 동기화합니다. 이미 존재하는 데이터는 좌표를 업데이트하고, 없는 데이터는 새로 추가합니다.")
    fun initializeAreas(): CommonResponse<List<AreaDto.AreaResponse>> {
        val domainResults = areaFacade.initializeAreas()
        val response = areaDtoMapper.toAreaResponses(domainResults)
        
        return CommonResponse.success(response)
    }
}
