package com.helloeva.integration.domain.auth

import com.helloeva.domain.user.domain.UserStatus
import com.helloeva.domain.user.infrastructure.UserRepository
import com.helloeva.integration.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import kotlin.test.assertEquals

@Transactional
class LogoutWithdrawTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jsonMapper: JsonMapper

    private fun login(kakaoAccessToken: String): String {
        val body = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"kakaoAccessToken": "$kakaoAccessToken"}"""
        }.andReturn().response.contentAsString
        return jsonMapper.readTree(body)["accessToken"].asString()
    }

    private fun logout(token: String?) =
        mockMvc.post("/auth/logout") { token?.let { header("Authorization", "Bearer $it") } }

    private fun withdraw(token: String) =
        mockMvc.post("/auth/withdraw") { header("Authorization", "Bearer $token") }

    @Test
    fun `AC-5 로그아웃하면 그 토큰으로 요청할 때 401 INVALID_TOKEN이다`() {
        val token = login("logout-user")

        logout(token).andExpect { status { isNoContent() } }

        logout(token).andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("INVALID_TOKEN") }
        }
    }

    @Test
    fun `AC-6 탈퇴하면 상태가 탈퇴가 되고 그 토큰으로 요청할 때 401 INVALID_TOKEN이다`() {
        val token = login("withdraw-user")

        withdraw(token).andExpect { status { isNoContent() } }

        logout(token).andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("INVALID_TOKEN") }
        }
        val user = userRepository.findAll().single { it.oauthInfo.providerId == "kakao-withdraw-user" }
        assertEquals(UserStatus.WITHDRAWN, user.status)
    }

    @Test
    fun `AC-7 탈퇴한 사용자가 같은 카카오 계정으로 로그인하면 새 계정이 생성된다`() {
        withdraw(login("rejoin-user"))

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"kakaoAccessToken": "rejoin-user"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.isNewUser") { value(true) }
        }

        val users = userRepository.findAll().filter { it.oauthInfo.providerId == "kakao-rejoin-user" }
        assertEquals(2, users.size)
        assertEquals(setOf(UserStatus.WITHDRAWN, UserStatus.ACTIVE), users.map { it.status }.toSet())
    }

    @Test
    fun `AC-8 토큰 없이 인증이 필요한 API를 부르면 401 UNAUTHORIZED다`() {
        logout(null).andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("UNAUTHORIZED") }
        }
    }
}
