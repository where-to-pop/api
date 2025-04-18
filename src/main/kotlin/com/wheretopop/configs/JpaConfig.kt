package com.wheretopop.config

import com.wheretopop.shared.converter.UniqueIdConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.boot.autoconfigure.domain.EntityScan

@Configuration
@EnableJpaRepositories(basePackages = ["com.wheretopop.infrastructure"])
@EntityScan(basePackages = ["com.wheretopop.infrastructure"])
class JpaConfig {
    
    /**
     * UniqueId 컨버터 빈 등록
     */
    @Bean
    fun uniqueIdConverter(): UniqueIdConverter {
        return UniqueIdConverter()
    }
} 