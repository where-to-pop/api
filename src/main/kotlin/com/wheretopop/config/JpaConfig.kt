package com.wheretopop.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.UUID
import javax.sql.DataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.util.Properties

/**
 * JPA 설정
 * 
 * 엔티티 스캔 경로와 리포지토리 스캔 경로를 설정합니다.
 * 생성일자와 수정일자를 자동으로 관리하기 위한 JPA Auditing 기능을 활성화합니다.
 */
@Configuration
@EnableJpaRepositories(basePackages = ["com.wheretopop.shared.infrastructure.repository"])
@EntityScan(basePackages = ["com.wheretopop.shared.infrastructure.entity"])
@EnableJpaAuditing
@EnableTransactionManagement
class JpaConfig {

    /**
     * 추가 DataSource 설정이 필요한 경우 여기에 추가할 수 있습니다.
     * 기본적으로는 application.yml의 설정을 사용합니다.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun dataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }
    
    /**
     * EntityManagerFactory 설정
     * Hibernate를 JPA 구현체로 사용합니다.
     */
    @Bean
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("com.wheretopop.shared.infrastructure.entity")
        
        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        
        val properties = Properties()
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
        properties.setProperty("hibernate.show_sql", "true")
        properties.setProperty("hibernate.format_sql", "true")
        properties.setProperty("hibernate.hbm2ddl.auto", "update")
        em.setJpaProperties(properties)
        
        return em
    }
    
    /**
     * JPA 트랜잭션 매니저 설정
     */
    @Bean
    fun transactionManager(entityManagerFactory: LocalContainerEntityManagerFactoryBean): JpaTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory.`object`
        return transactionManager
    }
    
    /**
     * UUID 타입 변환을 위한 설정
     * JPA에서 UUID를 처리하기 위한 설정을 추가할 수 있습니다.
     */
    @Bean
    fun uuidConverter(): java.util.function.Function<String, UUID> {
        return java.util.function.Function { it: String -> UUID.fromString(it) }
    }
} 