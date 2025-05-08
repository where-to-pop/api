package com.wheretopop.domain.popup

interface TextEmbeddingService {
    fun embed(text: String): FloatArray
}