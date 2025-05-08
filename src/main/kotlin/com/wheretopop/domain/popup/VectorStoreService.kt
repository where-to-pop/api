package com.wheretopop.domain.popup

interface VectorStoreService {
    fun addDocuments(documents: List<PopupReviewVectorDocument>)
}

data class PopupReviewVectorDocument(
    val id: String,
    val metadata: Map<String, Any>
)