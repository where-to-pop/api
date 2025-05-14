package com.wheretopop.domain.project

interface ProjectStore {
    suspend fun save(project: Project): Project
} 