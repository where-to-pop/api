package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.shared.domain.identifier.PopupPopplyId
import com.wheretopop.shared.infrastructure.entity.PopupPopplyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * JPA 팝업 Popply 저장소
 */
@Repository
interface JpaPopupPopplyRepository : JpaRepository<PopupPopplyEntity, PopupPopplyId> {
    fun findByPopupId(popupId: PopupId): PopupPopplyEntity?
    fun findByPopplyId(popplyId: Int): PopupPopplyEntity?
    
    @Query("SELECT p FROM PopupPopplyEntity p ORDER BY p.createdAt DESC")
    fun findAllOrderByCreatedAtDesc(): List<PopupPopplyEntity>
}

/**
 * 팝업 Popply 저장소 JPA 구현체
 */
@Repository
class JpaPopupPopplyRepositoryImpl(
    private val jpaRepository: JpaPopupPopplyRepository
) : PopupPopplyRepository {
    
    override fun save(entity: PopupPopplyEntity): PopupPopplyEntity =
        jpaRepository.save(entity)

    override fun save(entities: List<PopupPopplyEntity>): List<PopupPopplyEntity> =
        jpaRepository.saveAll(entities)

    override fun findAll(): List<PopupInfo> =
        jpaRepository.findAllOrderByCreatedAtDesc()
            .map { PopupPopplyEntity.toDomain(it) }

    override fun findByPopupId(popupId: PopupId): PopupPopplyEntity? =
        jpaRepository.findByPopupId(popupId)

    override fun findByPopplyId(popplyId: Int): PopupPopplyEntity? =
        jpaRepository.findByPopplyId(popplyId)
}