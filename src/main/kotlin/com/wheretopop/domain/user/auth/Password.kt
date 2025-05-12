package com.wheretopop.domain.user.auth

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@JvmInline
value class Password private constructor(
    val hashed: String
) {
    companion object {
        private val bcrypt = BCryptPasswordEncoder()

        /**
         * 일반적으로 사용하는 생성 메서드: 평문 또는 해시 모두 입력 가능
         */
        fun of(rawOrHashed: String): Password {
            return if (isBcryptHash(rawOrHashed)) {
                Password(rawOrHashed) // 이미 해시된 경우
            } else {
                validateRawPassword(rawOrHashed)
                Password(bcrypt.encode(rawOrHashed)) // 평문이면 해싱
            }
        }

        /**
         * DB에서 로딩 시 사용하는 생성 메서드
         */
        fun fromHashed(hashed: String): Password {
            require(isBcryptHash(hashed)) { "Not a valid bcrypt hash" }
            return Password(hashed)
        }

        /**
         * bcrypt 해시 여부 판단 (패턴 기반)
         */
        private fun isBcryptHash(value: String): Boolean {
            return value.startsWith("\$2a\$") || value.startsWith("\$2b\$") || value.startsWith("\$2y\$")
        }

        private fun validateRawPassword(password: String) {
            require(password.length >= 8) { "비밀번호는 최소 8자 이상이어야 합니다." }
            require(password.any { it.isDigit() }) { "비밀번호에는 숫자가 포함되어야 합니다." }
            require(password.any { it.isLetter() }) { "비밀번호에는 영문자가 포함되어야 합니다." }
        }
    }

    /**
     * 평문 입력과 해시 비교
     */
    fun matches(rawPassword: String): Boolean {
        return bcrypt.matches(rawPassword, hashed)
    }

    override fun toString(): String = "****" // toString()에서 실제 해시 노출 방지
}
