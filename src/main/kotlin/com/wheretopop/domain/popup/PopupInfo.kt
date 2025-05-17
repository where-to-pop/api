package com.wheretopop.domain.popup

import java.util.UUID

data class PopupInfo(
    val id: Long,
    val name: String,
    val address: String,
    val description: String,
    val organizerName: String,
) {
    fun getContentForEmbedding(): String {
        return listOf(name, address, organizerName, description).joinToString(separator = "\n")
    }

    fun buildVectorMetadataMap(): Map<String, Any> {
        return mapOf(
            "original_id" to this.id,
            "popup_name" to this.name,
            "address" to this.address,
            "organizer_name" to this.organizerName,
            "description" to this.description,
        )
    }

    fun generateVectorId(): String {
        return UUID.nameUUIDFromBytes(id.toString().toByteArray()).toString()
    }
}