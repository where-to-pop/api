package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId

interface ProjectService {
    fun createProject(command: ProjectCommand.Create): ProjectInfo.Main
    fun updateProject(command: ProjectCommand.Update): ProjectInfo.Main
    fun findProjectById(id: ProjectId): ProjectInfo.Main?
    fun findProjectsByOwnerId(ownerId: UserId): List<ProjectInfo.Main>
}