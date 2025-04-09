package com.wheretopop.shared.model

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime
import org.hibernate.annotations.Comment
import jakarta.persistence.Column

@MappedSuperclass   
@EntityListeners(AuditingEntityListener::class)
open class AbstractEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성 시간")
    open var createdAt: ZonedDateTime? = null

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 시간")
    open var updatedAt: ZonedDateTime? = null
}
