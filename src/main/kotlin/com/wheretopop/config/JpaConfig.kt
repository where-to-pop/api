package com.wheretopop.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JPA 설정
 * 
 * 엔티티 스캔 경로와 리포지토리 스캔 경로를 설정합니다.
 * 생성일자와 수정일자를 자동으로 관리하기 위한 JPA Auditing 기능을 활성화합니다.
 */
@Configuration
@EntityScan(basePackages = ["com.wheretopop.shared.infrastructure"])
@EnableJpaRepositories(basePackages = ["com.wheretopop.infrastructure"])
@EnableJpaAuditing
@EnableTransactionManagement
class JpaConfig(private val env: Environment) {

    /**
     * JPA 트랜잭션 매니저 설정
     */
    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }
}