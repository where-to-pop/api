package com.wheretopop.infrastructure.area.external.opendata


interface OpenDataApiCaller {
    fun support(type: OpenDataType): Boolean
    suspend fun callAndSave()
}