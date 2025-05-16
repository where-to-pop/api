package com.wheretopop.interfaces.user

import com.wheretopop.application.user.UserFacade
import com.wheretopop.application.user.UserInput
import com.wheretopop.config.security.AUTH_DELETE
import com.wheretopop.config.security.JwtProvider
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

/**
 * 인증(Auth) 관련 라우터 정의
 * Spring WebFlux 함수형 엔드포인트 사용
 */
@Configuration
class AuthApiRouter(private val authHandler: AuthHandler): RouterFunction<ServerResponse> {

    private val delegate = coRouter {
        "/v1/auth".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                // 로그인 (인증 불필요)
                POST("/login", authHandler::login)
                
                // 토큰 갱신 (인증 불필요)
                POST("/refresh", authHandler::refresh)
                
                // 로그아웃 (인증 필요)
                AUTH_DELETE("/logout") { request, userId ->
                    authHandler.logout(request, userId)
                }
            }
        }
    }
    
    override fun route(request: ServerRequest): Mono<HandlerFunction<ServerResponse>> = delegate.route(request)
}

/**
 * 인증(Auth) 관련 요청 처리 핸들러
 */
@Component
class AuthHandler(
    private val userFacade: UserFacade,
    private val jwtProvider: JwtProvider
) {
    
    private val ACCESS_TOKEN_COOKIE_NAME = "access_token"
    private val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
    private val SECURE_COOKIE = true  // HTTPS 환경에서는 true로 설정
    private val HTTP_ONLY = true
    private val SAME_SITE = "None"  // 크로스 도메인 쿠키 전송을 위해 None으로 설정
    
    /**
     * 쿠키 도메인을 요청 오리진에 따라 결정합니다.
     * 로컬호스트인 경우 도메인을 지정하지 않습니다.
     */
    private fun determineCookieDomain(request: ServerRequest): String? {
        val origin = request.headers().firstHeader("Origin") ?: ""
        return when {
            origin.contains("localhost") -> null // 로컬호스트인 경우 도메인 설정 안함
            origin.contains("where-to-pop.devkor.club") -> "where-to-pop.devkor.club"
            else -> null
        }
    }
    
    
    /**
     * 로그인 요청을 처리합니다.
     * 응답으로 액세스 토큰과 리프레시 토큰을 쿠키에 설정합니다.
     */
    suspend fun login(request: ServerRequest): ServerResponse {
        val loginRequest = request.awaitBody<AuthDto.LoginRequest>()
        
        val input = UserInput.Authenticate(
            identifier = loginRequest.email,
            rawPassword = loginRequest.password
        )
        
        // UserFacade를 통해 로그인 처리 (AuthInfo.Token 반환)
        val tokenInfo = userFacade.login(input)
        
        // 응답 데이터 생성
        val response = AuthDto.TokenResponse.from(tokenInfo)
        
        // 도메인 결정
        val domain = determineCookieDomain(request)
        
        // 리프레시 토큰 쿠키 생성
        val refreshTokenCookie = createTokenCookie(
            REFRESH_TOKEN_COOKIE_NAME, 
            tokenInfo.refreshToken,
            tokenInfo.refreshTokenExpiresAt,
            domain
        )

        // 액세스 토큰 쿠키 생성
        val accessTokenCookie = createTokenCookie(
            ACCESS_TOKEN_COOKIE_NAME,
            tokenInfo.accessToken,
            tokenInfo.accessTokenExpiresAt,
            domain
        )
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(refreshTokenCookie)
            .cookie(accessTokenCookie)
            .bodyValueAndAwait(CommonResponse.success(response))
    }
    
    /**
     * 토큰 갱신 요청을 처리합니다.
     * 쿠키에서 리프레시 토큰을 읽어 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     */
    suspend fun refresh(request: ServerRequest): ServerResponse {
        // 쿠키에서 리프레시 토큰 추출
        val refreshTokenCookie = request.cookies()[REFRESH_TOKEN_COOKIE_NAME]?.firstOrNull()
            ?: return ServerResponse.badRequest()
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        
        // 리프레시 토큰 유효성 검증
        val refreshToken = refreshTokenCookie.value
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ServerResponse.badRequest()
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
        
        // 토큰에서 사용자 ID 추출
        val userId = jwtProvider.getUserIdFromToken(refreshToken)
            ?: return ServerResponse.badRequest()
                .bodyValueAndAwait(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        
        // 리프레시 토큰 갱신 요청
        val input = UserInput.Refresh(
            rawRefreshToken = refreshToken,
            userId = userId
        )
        
        // UserFacade를 통해 토큰 갱신 처리
        val tokenInfo = userFacade.refresh(input)
        val response = AuthDto.TokenResponse.from(tokenInfo)
        
        // 도메인 결정
        val domain = determineCookieDomain(request)
        
        // 새 액세스 토큰 쿠키 생성
        val newAccessTokenCookie = createTokenCookie(
            ACCESS_TOKEN_COOKIE_NAME,
            tokenInfo.accessToken,
            tokenInfo.accessTokenExpiresAt,
            domain
        )

        // 새 리프레시 토큰 쿠키 생성
        val newRefreshTokenCookie = createTokenCookie(
            REFRESH_TOKEN_COOKIE_NAME,
            tokenInfo.refreshToken,
            tokenInfo.refreshTokenExpiresAt,
            domain
        )
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(newRefreshTokenCookie)
            .cookie(newAccessTokenCookie)
            .bodyValueAndAwait(CommonResponse.success(response))
    }
    
    /**
     * 로그아웃 요청을 처리합니다.
     * 액세스 토큰과 리프레시 토큰 쿠키를 만료시킵니다.
     */
    suspend fun logout(request: ServerRequest, userId: UserId): ServerResponse {
        // 도메인 결정
        val domain = determineCookieDomain(request)
        
        // 만료된 쿠키 생성
        val expiredRefreshCookie = createExpiredCookie(REFRESH_TOKEN_COOKIE_NAME, domain)
        val expiredAccessCookie = createExpiredCookie(ACCESS_TOKEN_COOKIE_NAME, domain)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(expiredRefreshCookie)
            .cookie(expiredAccessCookie)
            .bodyValueAndAwait(CommonResponse.success("로그아웃 되었습니다."))
    }


    /**
     * 토큰 쿠키를 생성하는 메서드
     */
    private fun createTokenCookie(
        name: String, 
        value: String, 
        expiresAt: Instant, 
        domain: String?
    ): ResponseCookie {
        val cookieBuilder = ResponseCookie.from(name, value)
            .maxAge(Duration.between(Instant.now(), expiresAt))
            .path("/")
            .secure(SECURE_COOKIE)
            .httpOnly(HTTP_ONLY)
            .sameSite(SAME_SITE)
        
        return if (domain != null) {
            cookieBuilder.domain(domain).build()
        } else {
            cookieBuilder.build()
        }
    }
    
    /**
     * 만료된 쿠키를 생성하는 메서드
     */
    private fun createExpiredCookie(name: String, domain: String?): ResponseCookie {
        val cookieBuilder = ResponseCookie.from(name, "")
            .maxAge(0)
            .path("/")
            .secure(SECURE_COOKIE)
            .httpOnly(HTTP_ONLY)
            .sameSite(SAME_SITE)
            
        return if (domain != null) {
            cookieBuilder.domain(domain).build()
        } else {
            cookieBuilder.build()
        }
    }
}