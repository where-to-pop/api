package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.shared.infrastructure.entity.PopupEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA 팝업 저장소
 */
@Repository
interface JpaPopupJpaRepository : JpaRepository<PopupEntity, PopupId> {
    fun findByName(name: String): PopupEntity?
}

/**
 * 팝업 저장소 JPA 구현체
 */
@Repository
class JpaPopupRepository(
    private val jpaRepository: JpaPopupJpaRepository
) : PopupRepository {
    
    override fun findById(id: PopupId): Popup? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByName(name: String): Popup? {
        return jpaRepository.findByName(name)?.toDomain()
    }

    override fun findAll(): List<Popup> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun save(popup: Popup): Popup {
        val entity = PopupEntity.of(popup)
        return jpaRepository.save(entity).toDomain()
    }

    override fun save(popups: List<Popup>): List<Popup> {
        val entities = popups.map { PopupEntity.of(it) }
        return jpaRepository.saveAll(entities).map { it.toDomain() }
    }

    override fun deleteById(id: PopupId) {
        jpaRepository.deleteById(id)
    }
}