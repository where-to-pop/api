package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.PopupReviewVectorDocument
import com.wheretopop.domain.popup.VectorStoreService
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class VectorStoreServiceImpl(
    private val vectorStore: VectorStore
) : VectorStoreService {

    override fun addDocuments(documents: List<PopupReviewVectorDocument>) {
        val springAiDocuments = documents.map { doc ->
            Document(
                doc.id,
                doc.metadata["full_text"] as String,
                doc.metadata
            )
        }
        vectorStore.add(springAiDocuments)
    }
}