package com.wheretopop.interfaces.project

import com.wheretopop.application.project.ProjectFacade
import com.wheretopop.config.security.CurrentUser
import com.wheretopop.config.security.UserPrincipal
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

/**
 * 프로젝트(Project) 관련 컨트롤러
 * Spring MVC 기반으로 구현
 */
@RestController
@RequestMapping("/v1/projects")
class ProjectController(private val projectFacade: ProjectFacade) {
    
    /**
     * 새로운 프로젝트를 생성합니다.
     */
    @PostMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(
        @RequestBody createRequest: ProjectDto.CreateRequest,
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ProjectDto.ProjectResponse> {
        val projectInfo = projectFacade.createProject(createRequest.toInput(principal.userId))
        
        return CommonResponse.success(ProjectDto.ProjectResponse.from(projectInfo))
    }
    
    /**
     * 프로젝트 정보를 수정합니다.
     */
    @PutMapping("/{projectId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProject(
        @PathVariable projectId: Long,
        @RequestBody updateRequest: ProjectDto.UpdateRequest,
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ProjectDto.ProjectResponse> {
        val projectInfo = projectFacade.updateProject(updateRequest.toInput(ProjectId.of(projectId)))
        
        return CommonResponse.success(ProjectDto.ProjectResponse.from(projectInfo))
    }
    
    /**
     * 프로젝트 상세 정보를 조회합니다.
     */
    @GetMapping("/{projectId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProject(
        @PathVariable projectId: Long,
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ProjectDto.ProjectResponse> {
        val projectInfo = projectFacade.findProjectById(ProjectId.of(projectId))
            ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
            
        return CommonResponse.success(ProjectDto.ProjectResponse.from(projectInfo))
    }
    
    /**
     * 현재 사용자의 프로젝트 목록을 조회합니다.
     */
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMyProjects(
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<List<ProjectDto.ProjectResponse>> {
        val projectInfos = projectFacade.findProjectsByOwnerId(principal.userId)
        
        return CommonResponse.success(
            projectInfos.map { ProjectDto.ProjectResponse.from(it) }
        )
    }
} 