package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.wheretopop.shared.response.CommonResponse
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

/**
 * 권역(Area) 관련 라우터 정의
 * Spring WebFlux 함수형 엔드포인트 사용
 */
@Configuration
class AreaApiRouter(private val areaHandler: AreaHandler):RouterFunction<ServerResponse> {

    private val delegate = coRouter {
        "/v1/areas".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                // 필터를 이용한 권역 정보 조회
                GET("", areaHandler::searchAreas)
            }
        }
    }
    override fun route(request: ServerRequest): Mono<HandlerFunction<ServerResponse>> = delegate.route(request)
}

/**
 * 권역(Area) 관련 요청 처리 핸들러
 */
@Component
class AreaHandler(private val areaFacade: AreaFacade) {
    
    private val areaDtoMapper = AreaDtoMapper()
    
    /**
     * 필터를 이용하여 권역 정보를 조회합니다.
     */
    suspend fun searchAreas(request: ServerRequest): ServerResponse {
        val searchRequest = AreaDto.SearchRequest(
            keyword = request.queryParam("keyword").orElse(null),
            regionId = request.queryParam("regionId").map { it.toLong() }.orElse(null),
            latitude = request.queryParam("latitude").map { it.toDouble() }.orElse(null),
            longitude = request.queryParam("longitude").map { it.toDouble() }.orElse(null),
            radius = request.queryParam("radius").map { it.toDouble() }.orElse(null),
            minFloatingPopulation = request.queryParam("minFloatingPopulation").map { it.toInt() }.orElse(null),
            minStoreCount = request.queryParam("minStoreCount").map { it.toInt() }.orElse(null),
            maxRent = request.queryParam("maxRent").map { it.toLong() }.orElse(null),
            offset = request.queryParam("offset").map { it.toInt() }.orElse(0),
            limit = request.queryParam("limit").map { it.toInt() }.orElse(20)
        )
        val criteria = areaDtoMapper.toCriteria(searchRequest)
        val domainResults = areaFacade.searchAreas(criteria)
        val response = areaDtoMapper.toAreaResponses(domainResults)

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(response))
    }
} 