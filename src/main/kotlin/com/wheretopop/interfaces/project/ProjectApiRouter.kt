package com.wheretopop.interfaces.project

import com.wheretopop.application.project.ProjectFacade
import com.wheretopop.config.security.AUTH_GET
import com.wheretopop.config.security.AUTH_POST
import com.wheretopop.config.security.AUTH_PUT
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

/**
 * 프로젝트(Project) 관련 라우터 정의
 * Spring WebFlux 함수형 엔드포인트 사용
 */
@Configuration
class ProjectApiRouter(private val projectHandler: ProjectHandler): RouterFunction<ServerResponse> {

    private val delegate = coRouter {
        "/v1/projects".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                // 프로젝트 생성 (인증 필요)
                AUTH_POST("") { request, userId ->
                    projectHandler.createProject(request, userId)
                }
                
                // 프로젝트 수정 (인증 필요)
                AUTH_PUT("/{projectId}") { request, userId ->
                    projectHandler.updateProject(request, userId)
                }
                
                // 프로젝트 상세 조회 (인증 필요)
                AUTH_GET("/{projectId}") { request, userId ->
                    projectHandler.getProject(request, userId)
                }
                
                // 사용자의 프로젝트 목록 조회 (인증 필요)
                AUTH_GET("") { request, userId ->
                    projectHandler.getMyProjects(request, userId)
                }
            }
        }
    }
    
    override fun route(request: ServerRequest): Mono<HandlerFunction<ServerResponse>> = delegate.route(request)
}

/**
 * 프로젝트(Project) 관련 요청 처리 핸들러
 */
@Component
class ProjectHandler(private val projectFacade: ProjectFacade) {
    
    /**
     * 새로운 프로젝트를 생성합니다.
     */
    suspend fun createProject(request: ServerRequest, userId: UserId): ServerResponse {
        val createRequest = request.awaitBody<ProjectDto.CreateRequest>()
        val projectInfo = projectFacade.createProject(createRequest.toInput(userId))
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(ProjectDto.ProjectResponse.from(projectInfo)))
    }
    
    /**
     * 프로젝트 정보를 수정합니다.
     */
    suspend fun updateProject(request: ServerRequest, userId: UserId): ServerResponse {
        val projectId = ProjectId.of(request.pathVariable("projectId").toLong())
        val updateRequest = request.awaitBody<ProjectDto.UpdateRequest>()
        val projectInfo = projectFacade.updateProject(updateRequest.toInput(projectId))
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(ProjectDto.ProjectResponse.from(projectInfo)))
    }
    
    /**
     * 프로젝트 상세 정보를 조회합니다.
     */
    suspend fun getProject(request: ServerRequest, userId: UserId): ServerResponse {
        val projectId = ProjectId.of(request.pathVariable("projectId").toLong())
        val projectInfo = projectFacade.findProjectById(projectId)
            ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
            
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(ProjectDto.ProjectResponse.from(projectInfo)))
    }
    
    /**
     * 현재 사용자의 프로젝트 목록을 조회합니다.
     */
    suspend fun getMyProjects(request: ServerRequest, userId: UserId): ServerResponse {
        val projectInfos = projectFacade.findProjectsByOwnerId(userId)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(
                projectInfos.map { ProjectDto.ProjectResponse.from(it) }
            ))
    }
} 