package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [AreaSyncScheduler::class])
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.ai.openai.api-key=test-key"
])
@DisplayName("AreaSyncScheduler 테스트")
class AreaSyncSchedulerTest {

    @Autowired
    private lateinit var areaSyncScheduler: AreaSyncScheduler

    @MockkBean
    private lateinit var areaFacade: AreaFacade

    @Test
    @DisplayName("스케줄러가 정상적으로 생성된다")
    fun testSchedulerCreation() {
        // Then
        assert(areaSyncScheduler != null)
    }

    @Test
    @DisplayName("스케줄러 메소드가 예외 없이 실행된다")
    fun testScheduleAreaExternalDataIngestion() {
        // Given
        every { areaFacade.ingestAreaExternalData() } just runs

        // When & Then - 예외가 발생하지 않는지만 확인
        try {
            areaSyncScheduler.scheduleAreaExternalDataIngestion()
            // 성공적으로 실행되면 테스트 통과
        } catch (e: Exception) {
            assert(false) { "스케줄러 실행 중 예외가 발생했습니다: ${e.message}" }
        }

        // AreaFacade가 호출되었는지 확인
        verify { areaFacade.ingestAreaExternalData() }
    }

    @Test
    @DisplayName("AreaSyncScheduler가 올바른 Component 어노테이션을 가진다")
    fun testComponentAnnotation() {
        // Given & When
        val componentAnnotation = AreaSyncScheduler::class.java
            .getAnnotation(org.springframework.stereotype.Component::class.java)
        
        // Then
        assert(componentAnnotation != null)
    }

    @Test
    @DisplayName("스케줄러 메소드가 Scheduled 어노테이션을 가진다")
    fun testScheduledAnnotation() {
        // Given & When
        val method = AreaSyncScheduler::class.java.getDeclaredMethod("scheduleAreaExternalDataIngestion")
        val scheduledAnnotation = method.getAnnotation(org.springframework.scheduling.annotation.Scheduled::class.java)
        
        // Then
        assert(scheduledAnnotation != null)
    }
} 