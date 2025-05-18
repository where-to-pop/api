package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId

interface ProjectReader {
    fun findById(id: ProjectId): Project?
    fun findByOwnerId(ownerId: UserId): List<Project>
} 