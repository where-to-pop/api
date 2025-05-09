package com.wheretopop.infrastructure.popup

import com.wheretopop.config.EmbeddingModel
import com.wheretopop.domain.popup.TextEmbeddingService
import org.springframework.stereotype.Service

@Service
class TextEmbeddingServiceImpl(
    private val embeddingModel: EmbeddingModel
) : TextEmbeddingService {
    override fun embed(text: String): FloatArray {
        return embeddingModel.embed(text)
    }
}