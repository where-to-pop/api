package com.wheretopop.infrastructure.popup

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

/**
 * 팝플리(Popply)에서 수집된 팝업스토어 정보를 위한 Elasticsearch 저장소
 */
@Repository
interface PopplyPopupRepository : ElasticsearchRepository<PopplyPopupDocument, String> {
    fun findByTitleContaining(title: String): List<PopplyPopupDocument>
    fun findByLocationContaining(location: String): List<PopplyPopupDocument>
} 