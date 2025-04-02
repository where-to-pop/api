package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.wheretopop.shared.response.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "구(Area)", description = "구(Area)에 대한 API")
@RequestMapping("/api/v1/areas")
class AreaApiController(
    private val areaFacade: AreaFacade,
) {
    private val areaDtoMapper = AreaDtoMapper()

    @GetMapping()
    @Operation(summary = "필터를 이용하여 구 정보를 조회합니다.")
    fun searchAreas(@ParameterObject request: AreaDto.SearchRequest): CommonResponse<List<AreaDto.AreaResponse>> {
        val criteria = areaDtoMapper.toCriteria(request)
        val domainResults = areaFacade.searchAreas(criteria)
        val response = areaDtoMapper.toAreaResponses(domainResults)

        return CommonResponse.success(response)
    }
}
