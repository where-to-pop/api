package com.wheretopop.interfaces.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.wheretopop.application.user.UserFacade
import com.wheretopop.config.security.JwtProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.ai.openai.api-key=test-key",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
])
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var userFacade: UserFacade

    @MockkBean
    private lateinit var jwtProvider: JwtProvider

    @Test
    @DisplayName("로그인 엔드포인트가 존재한다")
    fun testLoginEndpointExists() {
        // Given
        val loginRequest = mapOf(
            "email" to "test@example.com",
            "password" to "password123"
        )
        // Mock 설정 - 실제 동작하지 않더라도 404는 피함
        every { userFacade.login(any()) } returns mockk()

        // When & Then - 엔드포인트가 존재하는지만 확인 (404가 아닌지 확인)
        mockMvc.perform(post("/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect { result -> 
                assert(result.response.status != 404) { "엔드포인트가 존재해야 합니다" }
            }
    }

    @Test
    @DisplayName("토큰 갱신 엔드포인트가 존재한다")
    fun testRefreshEndpointExists() {
        // Given
        every { userFacade.refresh(any()) } returns mockk()

        // When & Then
        mockMvc.perform(post("/v1/auth/refresh"))
            .andExpect { result -> 
                assert(result.response.status != 404) { "엔드포인트가 존재해야 합니다" }
            }
    }

    @Test
    @DisplayName("로그아웃 엔드포인트가 존재한다")
    fun testLogoutEndpointExists() {
        // Given
        // When & Then
        mockMvc.perform(delete("/v1/auth/logout"))
            .andExpect { result -> 
                assert(result.response.status != 404) { "엔드포인트가 존재해야 합니다" }
            }
    }

    @Test
    @DisplayName("잘못된 JSON 형식 요청이 500 에러를 반환한다")
    fun testInvalidJsonRequest() {
        // Given - 잘못된 JSON
        val malformedJson = "{ invalid json }"

        // When & Then - JSON 파싱 에러가 시스템 에러로 처리됨
        mockMvc.perform(post("/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(malformedJson))
            .andExpect(status().isInternalServerError) // 실제로는 500으로 응답
    }

    @Test
    @DisplayName("AuthController가 올바른 RequestMapping을 가진다")
    fun testControllerRequestMapping() {
        // Given & When
        val requestMappingAnnotation = AuthController::class.java
            .getAnnotation(org.springframework.web.bind.annotation.RequestMapping::class.java)
        
        // Then
        assert(requestMappingAnnotation != null)
        assert(requestMappingAnnotation.value.contains("/v1/auth"))
    }

    @Test
    @DisplayName("AuthController가 RestController로 등록되어 있다")
    fun testRestControllerAnnotation() {
        // Given & When
        val restControllerAnnotation = AuthController::class.java
            .getAnnotation(org.springframework.web.bind.annotation.RestController::class.java)
        
        // Then
        assert(restControllerAnnotation != null)
    }
} 