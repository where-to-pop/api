package com.wheretopop.application.project

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.project.ProjectInfo
import com.wheretopop.domain.user.UserId

interface ProjectFacade {
    fun createProject(input: ProjectInput.Create): ProjectInfo.Main
    fun updateProject(input: ProjectInput.Update): ProjectInfo.Main
    fun findProjectById(id: ProjectId): ProjectInfo.Main?
    fun findProjectsByOwnerId(ownerId: UserId): List<ProjectInfo.Main>
}