package com.wheretopop.gateway.interfaces.area

import com.wheretopop.gateway.application.area.AreaFacade
import com.wheretopop.gateway.shared.response.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "구(AoI)", description = "구(Area)에 대한 API")
@RequestMapping("/api/v1/areas")
class AreaApiController(
    private val areaFacade: AreaFacade
) {

    @GetMapping()
    @Operation(summary = "get areas with filter")
    fun searchAreas(@ParameterObject request: AreaDto.SearchRequest): CommonResponse<AreaDto.ListResponse> {
        val results = areaFacade.searchAreas(request)
        return CommonResponse.success(results)
    }
}
