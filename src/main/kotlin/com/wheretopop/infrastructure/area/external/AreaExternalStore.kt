package com.wheretopop.infrastructure.area.external

interface AreaExternalStore {
    suspend fun callOpenDataApiAndSave()
}