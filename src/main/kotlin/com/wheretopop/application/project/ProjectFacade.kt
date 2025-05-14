package com.wheretopop.application.project

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.project.ProjectInfo
import com.wheretopop.domain.user.UserId

interface ProjectFacade {
    suspend fun createProject(input: ProjectInput.Create): ProjectInfo.Main
    suspend fun updateProject(input: ProjectInput.Update): ProjectInfo.Main
    suspend fun findProjectById(id: ProjectId): ProjectInfo.Main?
    suspend fun findProjectsByOwnerId(ownerId: UserId): List<ProjectInfo.Main>
}