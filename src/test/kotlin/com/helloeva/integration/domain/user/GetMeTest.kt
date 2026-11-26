package com.helloeva.integration.domain.user

import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.fake.FakeOauthClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper

@Transactional
class GetMeTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Test
    fun `AC-1 로그인한 사용자는 닉네임과 로그인 수단을 받는다`() {
        val body = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"kakaoAccessToken": "me-user"}"""
        }.andReturn().response.contentAsString
        val token = jsonMapper.readTree(body)["accessToken"].asString()

        mockMvc.get("/users/me") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.userId") { isNumber() }
            jsonPath("$.nickname") { value(FakeOauthClient.NICKNAME) }
            jsonPath("$.provider") { value("KAKAO") }
        }
    }
}
