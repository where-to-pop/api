package com.wheretopop.domain.user.auth

/**
 * 토큰을 발급, 조회, 저장, 삭제하는 인터페이스
 */
interface TokenManager {
    suspend fun issue(authUser: AuthUser): AuthInfo.Token
    suspend fun save(refreshToken: RefreshToken): RefreshToken
    suspend fun load(rawRefreshToken: String): RefreshToken?
}