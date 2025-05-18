package com.wheretopop.domain.user.auth

/**
 * 토큰을 발급, 조회, 저장, 삭제하는 인터페이스
 */
interface TokenManager {
    fun issue(authUser: AuthUser): AuthInfo.Token
    fun save(refreshToken: RefreshToken): RefreshToken
    fun load(rawRefreshToken: String): RefreshToken?
}