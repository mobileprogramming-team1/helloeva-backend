package com.helloeva.unit.common.security

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.common.security.JwtProperties
import com.helloeva.common.security.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import kotlin.test.assertEquals

class JwtTokenProviderTest {

    private val secret = "test-jwt-secret-key-for-helloeva-backend-tests-only"

    @Test
    fun `발급한 토큰에서 사용자 ID를 꺼낸다`() {
        val provider = JwtTokenProvider(JwtProperties(secret, Duration.ofDays(30)))

        val token = provider.createAccessToken(42L)

        assertEquals(42L, provider.parse(token).userId)
    }

    @Test
    fun `AC-4 유효 기간이 지난 토큰은 EXPIRED_TOKEN이다`() {
        val provider = JwtTokenProvider(JwtProperties(secret, Duration.ofDays(-1)))

        val token = provider.createAccessToken(42L)

        val e = assertThrows<CustomException> { provider.parse(token) }
        assertEquals(ErrorCode.EXPIRED_TOKEN, e.errorCode)
    }
}
