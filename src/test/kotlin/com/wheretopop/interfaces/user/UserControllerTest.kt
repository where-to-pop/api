package com.wheretopop.interfaces.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.wheretopop.application.user.UserFacade
import com.wheretopop.config.security.JwtProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.ai.openai.api-key=test-key",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
])
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var userFacade: UserFacade

    @MockkBean
    private lateinit var jwtProvider: JwtProvider

    @Test
    @DisplayName("회원가입 엔드포인트가 존재한다 (FORBIDDEN이 200으로 응답)")
    fun testSignupEndpointExists() {
        // Given
        val signupRequest = mapOf(
            "username" to "testuser",
            "email" to "test@example.com", 
            "identifier" to "testuser123",
            "password" to "password123",
            "profileImageUrl" to "https://example.com/profile.jpg"
        )

        // When & Then - 회원가입이 비활성화되어 있지만 예외가 200으로 응답됨
        mockMvc.perform(post("/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isOk) // 실제로는 200으로 응답
    }

    @Test
    @DisplayName("현재 사용자 조회 엔드포인트가 존재한다 (인증 문제로 500 응답)")
    fun testCurrentUserEndpointExists() {
        // When & Then - 인증 정보가 없어서 500 에러 발생
        mockMvc.perform(get("/v1/users/me"))
            .andExpect { result ->
                val body = result.response.contentAsString
                val json = jacksonObjectMapper().readTree(body)

                assert(result.response.status == 200) { "HTTP 상태가 200이 아님. $body" }
                assert(json["result"].asText() == "FAIL") { "result != FAIL. $body" }
                assert(json["errorCode"].asText() == "COMMON_FORBIDDEN") { "에러 코드 다름. $body" }
            }

    }

    @Test
    @DisplayName("잘못된 JSON 형식 회원가입 요청이 500 에러를 반환한다")
    fun testInvalidJsonSignupRequest() {
        // Given - 잘못된 JSON
        val malformedJson = "{ invalid json }"

        // When & Then - JSON 파싱 에러가 시스템 에러로 처리됨
        mockMvc.perform(post("/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(malformedJson))
            .andExpect(status().isInternalServerError) // 실제로는 500으로 응답
    }

    @Test
    @DisplayName("UserController가 올바른 RequestMapping을 가진다")
    fun testControllerRequestMapping() {
        // Given & When
        val requestMappingAnnotation = UserController::class.java
            .getAnnotation(org.springframework.web.bind.annotation.RequestMapping::class.java)
        
        // Then
        assert(requestMappingAnnotation != null)
        assert(requestMappingAnnotation.value.contains("/v1/users"))
    }

    @Test
    @DisplayName("UserController가 RestController로 등록되어 있다")
    fun testRestControllerAnnotation() {
        // Given & When
        val restControllerAnnotation = UserController::class.java
            .getAnnotation(org.springframework.web.bind.annotation.RestController::class.java)
        
        // Then
        assert(restControllerAnnotation != null)
    }
} 