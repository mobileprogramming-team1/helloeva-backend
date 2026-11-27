package com.helloeva.integration.domain.conversation

import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.infrastructure.AnswerRepository
import com.helloeva.domain.conversation.infrastructure.QuestionRepository
import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.loginAndGetToken
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDateTime

@Transactional
class ReplayQuestionTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Autowired
    lateinit var questionRepository: QuestionRepository

    @Autowired
    lateinit var answerRepository: AnswerRepository

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

    private fun replay(token: String, conversationId: Long, questionNumber: Int) =
        mockMvc.post("/conversations/$conversationId/questions/$questionNumber/replay") {
            header("Authorization", "Bearer $token")
        }

    private fun answer(conversationId: Long, questionNumber: Int) {
        val question = questionRepository.findByConversationIdAndQuestionNumber(conversationId, questionNumber)!!
        answerRepository.save(Answer(question, "my answer", "https://storage.example.com/a.m4a", 30, LocalDateTime.now()))
    }

    @Test
    fun `현재 문항에서 처음 다시 듣기를 요청하면 남은 횟수 0과 질문 음성 URL을 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "replay-ok")
        val id = createConversation(token)

        replay(token, id, 1).andExpect {
            status { isOk() }
            jsonPath("$.questionNumber") { value(1) }
            jsonPath("$.replayRemaining") { value(0) }
            jsonPath("$.questionAudioUrl") { isNotEmpty() }
        }
    }

    @Test
    fun `AC-5 다시 듣기를 두 번째로 요청하면 409 REPLAY_LIMIT_EXCEEDED를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "replay-ac5")
        val id = createConversation(token)
        replay(token, id, 1)

        replay(token, id, 1).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("REPLAY_LIMIT_EXCEEDED") }
        }
    }

    @Test
    fun `AC-9 이미 답한 문항에 다시 듣기를 요청하면 409 QUESTION_ALREADY_ANSWERED를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "replay-ac9")
        val id = createConversation(token)
        answer(id, 1)

        replay(token, id, 1).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("QUESTION_ALREADY_ANSWERED") }
        }
    }

    @Test
    fun `아직 차례가 아닌 문항에 다시 듣기를 요청하면 409 QUESTION_NOT_CURRENT를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "replay-not-current")
        val id = createConversation(token)

        replay(token, id, 3).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("QUESTION_NOT_CURRENT") }
        }
    }

    @Test
    fun `문항 번호가 1~15 범위 밖이면 404 QUESTION_NOT_FOUND를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "replay-not-found")
        val id = createConversation(token)

        replay(token, id, 16).andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("QUESTION_NOT_FOUND") }
        }
    }
}
