package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.wheretopop.shared.response.CommonResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

/**
 * 권역(Area) 관련 라우터 정의
 * Spring WebFlux 함수형 엔드포인트 사용
 */
@Configuration
class AreaApiRouter(private val areaHandler: AreaHandler) {

    @Bean
    fun areaRoutes() = router {
        "/v1/areas".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                // 필터를 이용한 권역 정보 조회
                GET("", areaHandler::searchAreas)
                
                // 기본 권역 데이터 초기화
                POST("/initialize", areaHandler::initializeAreas)
            }
        }
    }
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
    fun searchAreas(request: ServerRequest): Mono<ServerResponse> {
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
            .bodyValue(CommonResponse.success(response))
    }
    
    /**
     * 기본 권역 데이터를 초기화합니다.
     * 기본 권역 데이터를 동기화합니다. 이미 존재하는 데이터는 좌표를 업데이트하고, 없는 데이터는 새로 추가합니다.
     */
    fun initializeAreas(request: ServerRequest): Mono<ServerResponse> {
        val domainResults = areaFacade.initializeAreas()
        val response = areaDtoMapper.toAreaResponses(domainResults)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(CommonResponse.success(response))
    }
} 