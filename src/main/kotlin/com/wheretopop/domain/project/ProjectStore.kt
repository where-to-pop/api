package com.wheretopop.domain.project

interface ProjectStore {
    fun save(project: Project): Project
}