package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId

interface ProjectService {
    suspend fun createProject(command: ProjectCommand.Create): ProjectInfo.Main
    suspend fun updateProject(command: ProjectCommand.Update): ProjectInfo.Main
    suspend fun findProjectById(id: ProjectId): ProjectInfo.Main?
    suspend fun findProjectsByOwnerId(ownerId: UserId): List<ProjectInfo.Main>
} 