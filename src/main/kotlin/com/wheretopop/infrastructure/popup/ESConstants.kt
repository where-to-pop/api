package com.wheretopop.infrastructure.popup

/**
 * Elasticsearch 관련 상수
 */
object ESConstants {
    /**
     * Elasticsearch 인덱스 이름
     */
    object IndexNames {
        const val POPPLY_POPUPS = "popply_popups"
        const val INSTAGRAM_POPUPS = "instagram_popups" 
        const val NAVER_POPUPS = "naver_popups"
        const val COMBINED_POPUPS = "popups"
    }
    
    /**
     * 팝업스토어 정보 소스 타입
     */
    object SourceTypes {
        const val POPPLY = "POPPLY"
        const val INSTAGRAM = "INSTAGRAM"
        const val NAVER = "NAVER"
        const val OTHER = "OTHER"
    }
} 