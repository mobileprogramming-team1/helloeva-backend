package com.helloeva.integration.domain.conversation

import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.loginAndGetToken
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper

@Transactional
class GetConversationTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jsonMapper: JsonMapper

    private val survey = """
        {"job":"OFFICE_WORKER","student":"NO","recentCourse":"OVER_FIVE_YEARS_AGO","residence":"ALONE",
         "leisures":["MOVIES","PARKS","CAFES","SHOPPING"],"hobbies":["LISTENING_TO_MUSIC","READING"],
         "sports":["JOGGING","WALKING","GYM"],"travels":["STAYCATION","DOMESTIC_TRAVEL","OVERSEAS_TRAVEL"]}
    """.trimIndent()

    private fun createConversation(token: String): Long {
        val body = mockMvc.post("/conversations") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{"targetGrade": "IM3", "backgroundSurvey": $survey}"""
        }.andReturn().response.contentAsString
        return jsonMapper.readTree(body)["conversationId"].asLong()
    }

    @Test
    fun `AC-1 새로 만든 대화를 조회하면 15문항이 모두 오고 현재 문항은 1이며 질문 텍스트는 없다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "detail-ac1")
        val id = createConversation(token)

        mockMvc.get("/conversations/$id") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.conversationId") { value(id) }
            jsonPath("$.status") { value("IN_PROGRESS") }
            jsonPath("$.totalQuestionCount") { value(15) }
            jsonPath("$.currentQuestionNumber") { value(1) }
            jsonPath("$.questions.length()") { value(15) }
            (0..14).forEach { i ->
                jsonPath("$.questions[$i].questionNumber") { value(i + 1) }
                jsonPath("$.questions[$i].questionAudioUrl") { isNotEmpty() }
                jsonPath("$.questions[$i].questionText") { value(null) }
                jsonPath("$.questions[$i].replayRemaining") { value(1) }
                jsonPath("$.questions[$i].answer") { value(null) }
                jsonPath("$.questions[$i].feedback") { value(null) }
            }
        }
    }

    @Test
    fun `AC-7 다른 사용자의 대화에 접근하면 403을 받는다`() {
        val ownerToken = mockMvc.loginAndGetToken(jsonMapper, "detail-ac7-owner")
        val id = createConversation(ownerToken)
        val otherToken = mockMvc.loginAndGetToken(jsonMapper, "detail-ac7-other")

        mockMvc.get("/conversations/$id") { header("Authorization", "Bearer $otherToken") }.andExpect {
            status { isForbidden() }
            jsonPath("$.code") { value("NOT_CONVERSATION_OWNER") }
        }
    }

    @Test
    fun `없는 대화를 조회하면 404 CONVERSATION_NOT_FOUND를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "detail-not-found")

        mockMvc.get("/conversations/999999") { header("Authorization", "Bearer $token") }.andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("CONVERSATION_NOT_FOUND") }
        }
    }
}
