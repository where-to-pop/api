package com.wheretopop.application.project

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.project.ProjectInfo
import com.wheretopop.domain.project.ProjectService
import com.wheretopop.domain.user.UserId
import org.springframework.stereotype.Service

@Service
class ProjectFacadeImpl (
    private val projectService: ProjectService,
): ProjectFacade {
    override suspend fun createProject(input: ProjectInput.Create): ProjectInfo.Main {
        val command = input.toCommand()
        return projectService.createProject(command)
    }

    override suspend fun updateProject(input: ProjectInput.Update): ProjectInfo.Main {
        val command = input.toCommand()
        return projectService.updateProject(command)
    }


    override suspend fun findProjectById(id: ProjectId): ProjectInfo.Main? {
        return projectService.findProjectById(id)
    }

    override suspend fun findProjectsByOwnerId(ownerId: UserId): List<ProjectInfo.Main> {
        return projectService.findProjectsByOwnerId(ownerId)
    }
}