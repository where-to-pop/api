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
import java.net.URI
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
    private val HTTP_ONLY = true
    
    /**
     * 오리진 URL에서 도메인을 추출합니다.
     * 로컬호스트인 경우 null을 반환하고, 그 외는 호스트명을 반환합니다.
     */
    private fun extractDomainFromOrigin(origin: String): String? {
        if (origin.isBlank()) return null
        
        return try {
            val uri = URI(origin)
            val host = uri.host
            
            when {
                host.contains("localhost") -> null
                host.isNotBlank() -> host
                else -> null
            }
        } catch (e: Exception) {
            println("도메인 추출 오류: ${e.message}")
            null
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
        
        // 원본 요청의 오리진 추출
        val origin = request.headers().firstHeader("Origin") ?: ""
        println("Origin: $origin")
        
        // 오리진에서 도메인 추출
        val domain = extractDomainFromOrigin(origin)
        
        // 로컬호스트 체크
        val isLocalhost = origin.contains("localhost")
        
        // 리프레시 토큰 쿠키 생성
        val refreshTokenCookieBuilder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, tokenInfo.refreshToken)
            .maxAge(Duration.between(Instant.now(), tokenInfo.refreshTokenExpiresAt))
            .path("/")
            .secure(!isLocalhost) // 로컬호스트는 false, 그 외는 true
            .httpOnly(HTTP_ONLY)
            .sameSite(if (isLocalhost) "Lax" else "None")
        
        // 도메인이 null이 아닌 경우에만 도메인 설정
        val refreshTokenCookie = if (domain != null) {
            refreshTokenCookieBuilder.domain(domain).build()
        } else {
            refreshTokenCookieBuilder.build()
        }
            
        // 액세스 토큰 쿠키 생성
        val accessTokenCookieBuilder = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, tokenInfo.accessToken)
            .maxAge(Duration.between(Instant.now(), tokenInfo.accessTokenExpiresAt))
            .path("/")
            .secure(!isLocalhost) // 로컬호스트는 false, 그 외는 true
            .httpOnly(HTTP_ONLY)
            .sameSite(if (isLocalhost) "Lax" else "None")
            
        // 도메인이 null이 아닌 경우에만 도메인 설정
        val accessTokenCookie = if (domain != null) {
            accessTokenCookieBuilder.domain(domain).build()
        } else {
            accessTokenCookieBuilder.build()
        }
        
        // 로깅
        println("쿠키 설정: accessToken=${accessTokenCookie}, refreshToken=${refreshTokenCookie}")
        println("Domain: $domain, Secure: ${!isLocalhost}, SameSite: ${if (isLocalhost) "Lax" else "None"}")
            
        // 응답 생성
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Allow-Credentials", "true")
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
                
        // 쿠키 디버깅
        println("받은 쿠키: ${request.cookies().toString()}")
        
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
        
        // 원본 요청의 오리진 추출
        val origin = request.headers().firstHeader("Origin") ?: ""
        val isLocalhost = origin.contains("localhost")
        
        // 오리진에서 도메인 추출
        val domain = extractDomainFromOrigin(origin)
        
        // 새 액세스 토큰 쿠키 생성
        val newAccessTokenCookieBuilder = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, tokenInfo.accessToken)
            .maxAge(Duration.between(Instant.now(), tokenInfo.accessTokenExpiresAt))
            .path("/")
            .secure(!isLocalhost) // 로컬호스트는 false, 그 외는 true
            .httpOnly(HTTP_ONLY)
            .sameSite(if (isLocalhost) "Lax" else "None")
            
        // 도메인이 null이 아닌 경우에만 도메인 설정
        val newAccessTokenCookie = if (domain != null) {
            newAccessTokenCookieBuilder.domain(domain).build()
        } else {
            newAccessTokenCookieBuilder.build()
        }

        // 새 리프레시 토큰 쿠키 생성
        val newRefreshTokenCookieBuilder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, tokenInfo.refreshToken)
            .maxAge(Duration.between(Instant.now(), tokenInfo.refreshTokenExpiresAt))
            .path("/")
            .secure(!isLocalhost) // 로컬호스트는 false, 그 외는 true
            .httpOnly(HTTP_ONLY)
            .sameSite(if (isLocalhost) "Lax" else "None")
            
        // 도메인이 null이 아닌 경우에만 도메인 설정
        val newRefreshTokenCookie = if (domain != null) {
            newRefreshTokenCookieBuilder.domain(domain).build()
        } else {
            newRefreshTokenCookieBuilder.build()
        }
        
        // 로깅
        println("쿠키 설정: accessToken=${newAccessTokenCookie}, refreshToken=${newRefreshTokenCookie}")
        println("Domain: $domain, Secure: ${!isLocalhost}, SameSite: ${if (isLocalhost) "Lax" else "None"}")
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Allow-Credentials", "true")
            .cookie(newRefreshTokenCookie)
            .cookie(newAccessTokenCookie)
            .bodyValueAndAwait(CommonResponse.success(response))
    }
    
    /**
     * 로그아웃 요청을 처리합니다.
     * 액세스 토큰과 리프레시 토큰 쿠키를 만료시킵니다.
     */
    suspend fun logout(request: ServerRequest, userId: UserId): ServerResponse {
        // 원본 요청의 오리진 추출
        val origin = request.headers().firstHeader("Origin") ?: ""
        val isLocalhost = origin.contains("localhost")
        
        // 오리진에서 도메인 추출
        val domain = extractDomainFromOrigin(origin)
        
        // 리프레시 토큰 쿠키 만료
        val expiredRefreshCookieBuilder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
            .maxAge(0)
            .path("/")
            .secure(!isLocalhost)
            .httpOnly(HTTP_ONLY)
            .sameSite(if (isLocalhost) "Lax" else "None")
            
        // 도메인이 null이 아닌 경우에만 도메인 설정
        val expiredRefreshCookie = if (domain != null) {
            expiredRefreshCookieBuilder.domain(domain).build()
        } else {
            expiredRefreshCookieBuilder.build()
        }

        // 액세스 토큰 쿠키 만료
        val expiredAccessCookieBuilder = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
            .maxAge(0)
            .path("/")
            .secure(!isLocalhost)
            .httpOnly(HTTP_ONLY)
            .sameSite(if (isLocalhost) "Lax" else "None")
            
        // 도메인이 null이 아닌 경우에만 도메인 설정
        val expiredAccessCookie = if (domain != null) {
            expiredAccessCookieBuilder.domain(domain).build()
        } else {
            expiredAccessCookieBuilder.build()
        }
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Allow-Credentials", "true")
            .cookie(expiredRefreshCookie)
            .cookie(expiredAccessCookie)
            .bodyValueAndAwait(CommonResponse.success("로그아웃 되었습니다."))
    }
}   