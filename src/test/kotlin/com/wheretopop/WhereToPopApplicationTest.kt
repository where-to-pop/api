package com.wheretopop

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [WhereToPopApplication::class])
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.ai.openai.api-key=test-key",
    "spring.ai.openai.model.chat.enabled=false"
])
@DisplayName("WhereToPopApplication 테스트")
class WhereToPopApplicationTest {
//
//    @Test
//    @DisplayName("Spring Boot 애플리케이션 컨텍스트가 정상적으로 로드된다")
//    fun contextLoads() {
//        // Spring Boot 애플리케이션이 정상적으로 시작되는지 확인
//        // 별도의 assertion 없이도 컨텍스트 로딩 실패 시 테스트가 실패함
//    }
//
//    @Test
//    @DisplayName("main 메소드가 정상적으로 실행된다")
//    fun mainMethodTest() {
//        // main 메소드 호출 테스트
//        // 실제 애플리케이션 실행 없이 메소드 존재 여부만 확인
//        val mainMethod = WhereToPopApplication::class.java.getDeclaredMethod("main", Array<String>::class.java)
//        assert(mainMethod != null)
//    }
} 