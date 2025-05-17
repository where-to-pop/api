package com.wheretopop.interfaces.user

import com.wheretopop.application.user.UserFacade
import com.wheretopop.application.user.UserInput
import com.wheretopop.config.security.JwtProvider
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.Duration
import java.time.Instant

/**
 * 인증(Auth) 관련 컨트롤러
 * Spring MVC 기반으로 구현
 */
@RestController
@RequestMapping("/v1/auth")
class AuthController(
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
     * 주어진 도메인이 안전한 Public Suffix인지 확인합니다.
     * 일부 브라우저는 .dev, .app, .club 등의 도메인에서 쿠키 설정에 제한이 있을 수 있습니다.
     */
    private fun shouldPrefixWithDot(domain: String): Boolean {
        val publicSuffixes = listOf(".com", ".org", ".net", ".club", ".dev", ".app", ".io")
        return publicSuffixes.any { domain.endsWith(it) }
    }
    
    /**
     * 로그인 요청을 처리합니다.
     * 응답으로 액세스 토큰과 리프레시 토큰을 쿠키에 설정합니다.
     */
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: AuthDto.LoginRequest, @RequestHeader(value = "Origin", required = false) origin: String?): ResponseEntity<CommonResponse<AuthDto.TokenResponse>> {
        val input = UserInput.Authenticate(
            identifier = loginRequest.email,
            rawPassword = loginRequest.password
        )
        
        // UserFacade를 통해 로그인 처리 (AuthInfo.Token 반환)
        val tokenInfo = userFacade.login(input)
        
        // 응답 데이터 생성
        val response = AuthDto.TokenResponse.from(tokenInfo)
        
        // 원본 요청의 오리진 추출
        val originUrl = origin ?: ""
        println("Origin: $originUrl")
        
        // 오리진에서 도메인 추출
        var domain = extractDomainFromOrigin(originUrl)
        
        // 일부 도메인에서는 앞에 점을 추가해 모든 서브도메인에서 사용 가능하게 함
        if (domain != null && shouldPrefixWithDot(domain) && !domain.startsWith(".")) {
            // 이미 점으로 시작하는 경우 중복으로 추가하지 않음
            domain = ".$domain"
        }
        
        // 로컬호스트 체크
        val isLocalhost = originUrl.contains("localhost")
        
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
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Allow-Origin", originUrl)  // 클라이언트 오리진에 맞춤
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization")
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(CommonResponse.success(response))
    }
    
    /**
     * 토큰 갱신 요청을 처리합니다.
     * 쿠키에서 리프레시 토큰을 읽어 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     */
    @PostMapping("/refresh")
    fun refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) refreshToken: String?, 
                @RequestHeader(value = "Origin", required = false) origin: String?): ResponseEntity<CommonResponse<*>> {
        // 쿠키에서 리프레시 토큰 추출
        if (refreshToken == null) {
            return ResponseEntity.badRequest()
                .body(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
                
        // 리프레시 토큰 유효성 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest()
                .body(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        }
        
        // 토큰에서 사용자 ID 추출
        val userId = jwtProvider.getUserIdFromToken(refreshToken)
            ?: return ResponseEntity.badRequest()
                .body(CommonResponse.fail(ErrorCode.AUTH_INVALID_TOKEN))
        
        // 리프레시 토큰 갱신 요청
        val input = UserInput.Refresh(
            rawRefreshToken = refreshToken,
            userId = userId
        )
        
        // UserFacade를 통해 토큰 갱신 처리
        val tokenInfo = userFacade.refresh(input)
        val response = AuthDto.TokenResponse.from(tokenInfo)
        
        // 원본 요청의 오리진 추출
        val originUrl = origin ?: ""
        val isLocalhost = originUrl.contains("localhost")
        
        // 오리진에서 도메인 추출
        var domain = extractDomainFromOrigin(originUrl)
        
        // 일부 도메인에서는 앞에 점을 추가해 모든 서브도메인에서 사용 가능하게 함
        if (domain != null && shouldPrefixWithDot(domain) && !domain.startsWith(".")) {
            domain = ".$domain"
        }
        
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
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Allow-Origin", originUrl)  // 클라이언트 오리진에 맞춤
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization")
            .header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString())
            .body(CommonResponse.success(response))
    }
    
    /**
     * 로그아웃 요청을 처리합니다.
     * 액세스 토큰과 리프레시 토큰 쿠키를 만료시킵니다.
     */
    @DeleteMapping("/logout")
    fun logout(@RequestAttribute("userId") userId: UserId,
               @RequestHeader(value = "Origin", required = false) origin: String?): ResponseEntity<CommonResponse<String>> {
        // 원본 요청의 오리진 추출
        val originUrl = origin ?: ""
        val isLocalhost = originUrl.contains("localhost")
        
        // 오리진에서 도메인 추출
        var domain = extractDomainFromOrigin(originUrl)
        
        // 일부 도메인에서는 앞에 점을 추가해 모든 서브도메인에서 사용 가능하게 함
        if (domain != null && shouldPrefixWithDot(domain) && !domain.startsWith(".")) {
            domain = ".$domain"
        }
        
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
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Allow-Origin", originUrl)  // 클라이언트 오리진에 맞춤
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization")
            .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString())
            .header(HttpHeaders.SET_COOKIE, expiredAccessCookie.toString())
            .body(CommonResponse.success("로그아웃 되었습니다."))
    }
}   