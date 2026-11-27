package com.helloeva.integration.domain.conversation

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import com.helloeva.domain.feedback.domain.AreaScores
import com.helloeva.domain.feedback.domain.OverallFeedback
import com.helloeva.domain.feedback.infrastructure.OverallFeedbackRepository
import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.loginAndGetToken
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper

@Transactional
class GetConversationsTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Autowired
    lateinit var conversationRepository: ConversationRepository

    @Autowired
    lateinit var overallFeedbackRepository: OverallFeedbackRepository

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

    private fun complete(conversationId: Long, grade: Grade) {
        val conversation = conversationRepository.findById(conversationId).get()
        conversation.complete()
        overallFeedbackRepository.save(
            OverallFeedback(
                conversation, grade, AreaScores(70, 70, 70, 70),
                listOf("s"), listOf("w"), listOf("r"), listOf("f"), "bye",
            ),
        )
    }

    private fun prepareFourConversations(kakaoToken: String): String {
        val token = mockMvc.loginAndGetToken(jsonMapper, kakaoToken)
        val ids = (1..4).map { createConversation(token) }
        ids.take(3).forEach { complete(it, Grade.IM2) }
        return token
    }

    @Test
    fun `AC-1 전체를 조회하면 회차 내림차순으로 4개가 오고 개수는 4다`() {
        val token = prepareFourConversations("list-ac1")

        mockMvc.get("/conversations") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.totalCount") { value(4) }
            listOf(4, 3, 2, 1).forEachIndexed { i, round -> jsonPath("$.items[$i].round") { value(round) } }
            jsonPath("$.items[0].status") { value("IN_PROGRESS") }
            jsonPath("$.items[0].overallEstimatedGrade") { value(null) }
            jsonPath("$.items[0].createdAt") { value(matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*\\+09:00")) }
        }
    }

    @Test
    fun `AC-2 완료만 조회하면 3개가 오고 각각 종합 예상 등급이 있다`() {
        val token = prepareFourConversations("list-ac2")

        mockMvc.get("/conversations") {
            header("Authorization", "Bearer $token")
            param("status", "COMPLETED")
        }.andExpect {
            status { isOk() }
            jsonPath("$.totalCount") { value(3) }
            (0..2).forEach { i ->
                jsonPath("$.items[$i].status") { value("COMPLETED") }
                jsonPath("$.items[$i].overallEstimatedGrade") { value("IM2") }
            }
        }
    }

    @Test
    fun `AC-3 대화가 없는 사용자는 빈 목록과 개수 0을 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "list-ac3")

        mockMvc.get("/conversations") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.totalCount") { value(0) }
            jsonPath("$.items") { isEmpty() }
        }
    }

    @Test
    fun `AC-4 다른 사용자의 대화는 내 목록에 나오지 않는다`() {
        prepareFourConversations("list-ac4-other")
        val token = mockMvc.loginAndGetToken(jsonMapper, "list-ac4-me")

        mockMvc.get("/conversations") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.totalCount") { value(0) }
        }
    }
}
