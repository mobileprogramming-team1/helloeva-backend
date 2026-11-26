package com.helloeva.integration.domain.auth

import com.helloeva.domain.user.infrastructure.UserRepository
import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.fake.FakeOauthClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@Transactional
class LoginTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    private fun login(kakaoAccessToken: String) =
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"kakaoAccessToken": "$kakaoAccessToken"}"""
        }

    @Test
    fun `AC-1 처음 로그인하면 계정이 생성되고 isNewUser가 true다`() {
        login("new-user").andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { isNotEmpty() }
            jsonPath("$.isNewUser") { value(true) }
        }

        val users = userRepository.findAll().filter { it.oauthInfo.providerId == "kakao-new-user" }
        assertEquals(1, users.size)
        assertEquals(FakeOauthClient.NICKNAME, users[0].nickname)
    }

    @Test
    fun `AC-2 이미 가입한 사용자가 로그인하면 새 계정 없이 isNewUser가 false다`() {
        login("existing-user")

        login("existing-user").andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { isNotEmpty() }
            jsonPath("$.isNewUser") { value(false) }
        }

        assertEquals(1, userRepository.findAll().count { it.oauthInfo.providerId == "kakao-existing-user" })
    }

    @Test
    fun `AC-3 잘못된 카카오 토큰이면 401 INVALID_KAKAO_TOKEN이다`() {
        login(FakeOauthClient.INVALID_TOKEN).andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("INVALID_KAKAO_TOKEN") }
        }
    }

    @Test
    fun `AC-9 카카오 서버 오류면 502 KAKAO_SERVER_ERROR다`() {
        login(FakeOauthClient.SERVER_ERROR_TOKEN).andExpect {
            status { isBadGateway() }
            jsonPath("$.code") { value("KAKAO_SERVER_ERROR") }
        }
    }
}
