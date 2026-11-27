package com.helloeva.integration.domain.feedback

import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.infrastructure.AnswerRepository
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import com.helloeva.domain.conversation.infrastructure.QuestionRepository
import com.helloeva.domain.feedback.infrastructure.OverallFeedbackRepository
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
import java.time.LocalDateTime
import kotlin.test.assertEquals

@Transactional
class OverallFeedbackTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Autowired
    lateinit var questionRepository: QuestionRepository

    @Autowired
    lateinit var answerRepository: AnswerRepository

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
            content = """{"targetGrade": "AL", "backgroundSurvey": $survey}"""
        }.andReturn().response.contentAsString
        return jsonMapper.readTree(body)["conversationId"].asLong()
    }

    private fun answerDirectly(conversationId: Long, questionNumbers: IntRange) {
        questionNumbers.forEach {
            val question = questionRepository.findByConversationIdAndQuestionNumber(conversationId, it)!!
            answerRepository.save(Answer(question, "answer $it", "https://storage.example.com/$it.m4a", 30, LocalDateTime.now()))
        }
    }

    private fun createFeedback(token: String, conversationId: Long) =
        mockMvc.post("/conversations/$conversationId/overall-feedback") { header("Authorization", "Bearer $token") }

    private fun getFeedback(token: String, conversationId: Long) =
        mockMvc.get("/conversations/$conversationId/overall-feedback") { header("Authorization", "Bearer $token") }

    @Test
    fun `AC-1 15문항을 모두 답한 대화의 종합 피드백을 요청하면 모든 항목을 받고 대화는 완료가 된다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "overall-ac1")
        val id = createConversation(token)
        answerDirectly(id, 1..15)

        createFeedback(token, id).andExpect {
            status { isCreated() }
            jsonPath("$.conversationId") { value(id) }
            jsonPath("$.totalQuestionCount") { value(15) }
            jsonPath("$.overallEstimatedGrade") { value("IM3") }
            jsonPath("$.targetGrade") { value("AL") }
            jsonPath("$.areaScores.fluency") { value(70) }
            jsonPath("$.areaScores.organization") { value(75) }
            jsonPath("$.strengths") { isNotEmpty() }
            jsonPath("$.weaknesses") { isNotEmpty() }
            jsonPath("$.recommendedStudies") { isNotEmpty() }
            jsonPath("$.frequentExpressions") { isNotEmpty() }
            jsonPath("$.closingMessage") { isNotEmpty() }
        }

        assertEquals(ConversationStatus.COMPLETED, conversationRepository.findById(id).get().status)
    }

    @Test
    fun `AC-2 만들어진 종합 피드백을 조회하면 같은 내용을 받고 영역 점수는 0~100이다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "overall-ac2")
        val id = createConversation(token)
        answerDirectly(id, 1..15)
        createFeedback(token, id)

        getFeedback(token, id).andExpect {
            status { isOk() }
            jsonPath("$.overallEstimatedGrade") { value("IM3") }
            jsonPath("$.areaScores.fluency") { value(70) }
            jsonPath("$.areaScores.vocabulary") { value(65) }
            jsonPath("$.areaScores.grammar") { value(60) }
            jsonPath("$.areaScores.organization") { value(75) }
            jsonPath("$.closingMessage") { value("오늘 정말 고생 많았어요. 다음 모의고사에서 또 만나요!") }
        }
    }

    @Test
    fun `AC-3 15문항을 다 답하지 않은 대화는 409 QUESTIONS_NOT_ALL_ANSWERED를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "overall-ac3")
        val id = createConversation(token)
        answerDirectly(id, 1..14)

        createFeedback(token, id).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("QUESTIONS_NOT_ALL_ANSWERED") }
        }
    }

    @Test
    fun `AC-4 종합 피드백을 만들지 않은 대화를 조회하면 409 OVERALL_FEEDBACK_NOT_REQUESTED를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "overall-ac4")
        val id = createConversation(token)

        getFeedback(token, id).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("OVERALL_FEEDBACK_NOT_REQUESTED") }
        }
    }

    @Test
    fun `AC-5 종합 피드백이 이미 있는 대화에 다시 요청하면 새로 만들지 않고 기존 결과를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "overall-ac5")
        val id = createConversation(token)
        answerDirectly(id, 1..15)
        createFeedback(token, id)

        createFeedback(token, id).andExpect {
            status { isOk() }
            jsonPath("$.overallEstimatedGrade") { value("IM3") }
        }

        assertEquals(1, overallFeedbackRepository.findAll().count { it.conversation.id == id })
    }

    @Test
    fun `다른 사용자의 대화에 종합 피드백을 요청하면 403을 받는다`() {
        val ownerToken = mockMvc.loginAndGetToken(jsonMapper, "overall-owner")
        val id = createConversation(ownerToken)
        val otherToken = mockMvc.loginAndGetToken(jsonMapper, "overall-other")

        createFeedback(otherToken, id).andExpect {
            status { isForbidden() }
            jsonPath("$.code") { value("NOT_CONVERSATION_OWNER") }
        }
    }
}
