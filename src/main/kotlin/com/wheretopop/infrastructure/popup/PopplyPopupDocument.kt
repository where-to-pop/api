package com.wheretopop.infrastructure.popup

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

/**
 * 팝플리(Popply)에서 수집된 팝업스토어 정보 Elasticsearch 문서
 * 
 * 팝플리는 국내 팝업스토어 정보를 제공하는 전문 사이트로,
 * 해당 사이트에서 크롤링된 데이터를 저장하는 인덱스입니다.
 * 이 문서는 Python 크롤러에서 생성된 Elasticsearch 인덱스와 매핑됩니다.
 */
@Document(indexName = ESConstants.IndexNames.POPPLY_POPUPS)
data class PopplyPopupDocument(
    @Id
    val id: String? = null,
    
    @Field(type = FieldType.Text)
    val title: String,
    
    @Field(type = FieldType.Text)
    val location: String? = null,
    
    @Field(type = FieldType.Text)
    val period: String? = null,
    
    @Field(type = FieldType.Keyword)
    val link: String,
    
    @Field(type = FieldType.Date, name = "created_at")
    val createdAt: LocalDateTime,
    
    @Field(type = FieldType.Keyword)
    val source: String = "POPPLY"
) 