package com.wheretopop.infrastructure.area.external.opendata


interface OpenDataProcessor {
    fun support(type: OpenDataType): Boolean
    fun callAndSave()
}