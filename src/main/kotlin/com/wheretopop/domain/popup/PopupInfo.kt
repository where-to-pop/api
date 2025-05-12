package com.wheretopop.domain.popup

data class PopupInfo(
    val id: Long,
    val name: String,
    val address: String,
    val description: String,
    val organizerName: String
) {
    fun getContentForEmbedding(): String {
        return listOf(name, address, organizerName, description).joinToString(separator = "\n")
    }

    fun buildVectorMetadataMap(): Map<String, Any> {
        return mapOf(
            "popup_name" to this.name,
            "address" to this.address,
            "organizer_name" to this.organizerName,
        )
    }
}