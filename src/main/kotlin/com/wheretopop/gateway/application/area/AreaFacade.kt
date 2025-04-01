package com.wheretopop.gateway.application.area

import com.wheretopop.gateway.interfaces.area.AreaDto
import com.wheretopop.gateway.shared.exception.SystemErrorException
import org.springframework.stereotype.Service

@Service
class AreaFacade {
//    private val areaService: AreaService
    fun searchAreas(request: AreaDto.SearchRequest): AreaDto.ListResponse {
        throw  SystemErrorException("not implemented yet")
    }
}