package com.wheretopop.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Spring Security 설정 클래스
 * Spring MVC 환경에서의 보안 설정을 담당합니다.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtProvider: JwtProvider
) {
    /**
     * 비밀번호 암호화를 위한 인코더를 설정합니다.
     */
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    /**
     * JWT 기반 인증 필터를 설정합니다.
     */
    @Bean
    fun jwtAuthenticationFilter() = JwtAuthenticationFilter(jwtProvider)

    /**
     * Spring Security 필터 체인 설정
     * REST API 보안 규칙 및 JWT 인증 설정
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // CSRF 비활성화 (REST API는 CSRF가 필요 없음)
        http.csrf { it.disable() }
            
        // 세션 관리 정책 설정 (STATELESS = 세션 사용 안함)
        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            
        // 요청 경로별 인증 규칙 설정
        http.authorizeHttpRequests { auth ->
            // 인증 불필요 경로
            auth.requestMatchers(
                "/v1/auth/login",
                "/v1/auth/refresh",
                "/v1/users",  // 회원가입
                "/v1/swagger-ui/**",
                "/v1/api-docs/**",
                "/error",
                "/mcp/**",    // Spring AI MCP 엔드포인트
                "/actuator/**"
            ).permitAll()
                
            // OPTIONS 요청은 인증 불필요 (CORS preflight 요청)
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
            // 그 외 모든 요청은 인증 필요
            auth.anyRequest().authenticated()
        }
        
        // JWT 인증 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            
        return http.build()
    }
}

/**
 * JWT 인증 필터
 * 요청에서 JWT를 검증하고 Spring Security 인증 객체를 설정합니다.
 */
class JwtAuthenticationFilter(private val jwtProvider: JwtProvider) : org.springframework.web.filter.OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain
    ) {
        try {
            // 헤더에서 JWT 토큰 추출
            val jwt = getJwtFromRequest(request)
            
            // 토큰이 유효하면 Authentication 객체 생성 및 SecurityContext에 설정
            if (jwt != null && jwtProvider.validateAccessToken(jwt)) {
                val userId = jwtProvider.getUserIdFromToken(jwt)
                if (userId != null) {
                    val auth = jwtProvider.getAuthentication(userId)
                    org.springframework.security.core.context.SecurityContextHolder.getContext().authentication = auth
                    
                    // 사용자 ID를 요청 속성으로 저장 (컨트롤러에서 @RequestAttribute로 접근 가능)
                    request.setAttribute("userId", userId)
                    request.setAttribute(JwtAuthenticationConverter.AUTH_STATUS, "VALID")
                    request.setAttribute(JwtAuthenticationConverter.AUTH_USER_ID, userId)
                }
            }
        } catch (ex: Exception) {
            // 로깅 추가
            println("JWT 인증 중 오류 발생: ${ex.message}")
        }
        
        filterChain.doFilter(request, response)
    }
    
    /**
     * 요청에서 JWT 토큰을 추출합니다.
     * Authorization 헤더 또는 쿠키에서 토큰을 찾습니다.
     */
    private fun getJwtFromRequest(request: jakarta.servlet.http.HttpServletRequest): String? {
        // Authorization 헤더에서 토큰 추출 시도
        val bearerToken = request.getHeader("Authorization")
        if (!bearerToken.isNullOrBlank() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        
        // 쿠키에서 토큰 추출 시도
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "access_token") {
                    return cookie.value
                }
            }
        }
        
        return null
    }
}