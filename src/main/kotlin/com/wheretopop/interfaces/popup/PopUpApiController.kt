package com.wheretopop.interfaces.popup

import com.wheretopop.shared.response.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "팝업스토어(PopUp)", description = "팝업스토어(PopUp)에 대한 API")
@RequestMapping("/v1/popups")
class PopUpApiController{
    @GetMapping
    @Operation(summary = "필터를 이용하여 팝업스토어 정보를 조회합니다.")   
    fun searchPopUps(@ParameterObject request: PopUpDto.SearchRequest): CommonResponse<List<PopUpDto.PopUpResponse>> {
        // val criteria = popUpDtoMapper.toCriteria(request)
        // val domainResults = popUpFacade.searchPopUps(criteria)
        // val response = popUpDtoMapper.toPopUpResponses(domainResults)

        return CommonResponse.success(emptyList())
    }
}
