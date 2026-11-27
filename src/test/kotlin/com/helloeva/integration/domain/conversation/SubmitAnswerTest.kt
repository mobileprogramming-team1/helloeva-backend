package com.helloeva.integration.domain.conversation

import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.infrastructure.AnswerRepository
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import com.helloeva.domain.conversation.infrastructure.QuestionRepository
import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.fake.FakeSpeechToText
import com.helloeva.integration.loginAndGetToken
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDateTime
import kotlin.test.assertEquals

@Transactional
class SubmitAnswerTest : IntegrationTest() {

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

    private val survey = """
        {"job":"OFFICE_WORKER","student":"NO","recentCourse":"OVER_FIVE_YEARS_AGO","residence":"ALONE",
         "leisures":["MOVIES","PARKS","CAFES","SHOPPING"],"hobbies":["LISTENING_TO_MUSIC","READING"],
         "sports":["JOGGING","WALKING","GYM"],"travels":["STAYCATION","DOMESTIC_TRAVEL","OVERSEAS_TRAVEL"]}
    """.trimIndent()

    private fun createConversation(token: String): Long {
        val body = mockMvc.post("/conversations") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{"targetGrade": "IH", "backgroundSurvey": $survey}"""
        }.andReturn().response.contentAsString
        return jsonMapper.readTree(body)["conversationId"].asLong()
    }

    private fun submit(
        token: String,
        conversationId: Long,
        questionNumber: Int,
        filename: String = "answer.m4a",
        content: ByteArray = "audio".toByteArray(),
    ) = mockMvc.multipart("/conversations/$conversationId/questions/$questionNumber/answer") {
        file(MockMultipartFile("audio", filename, "audio/mp4", content))
        header("Authorization", "Bearer $token")
    }

    private fun answerDirectly(conversationId: Long, questionNumbers: IntRange) {
        questionNumbers.forEach {
            val question = questionRepository.findByConversationIdAndQuestionNumber(conversationId, it)!!
            answerRepository.save(Answer(question, "answer $it", "https://storage.example.com/$it.m4a", 30, LocalDateTime.now()))
        }
    }

    @Test
    fun `AC-2 1번에 음성 답변을 제출하면 질문 텍스트·답변 텍스트·답변 분석을 받고 현재 문항은 2가 된다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-ac2")
        val id = createConversation(token)

        submit(token, id, 1).andExpect {
            status { isCreated() }
            jsonPath("$.questionNumber") { value(1) }
            jsonPath("$.questionText") { value("Fake question 1") }
            jsonPath("$.answer.transcript") { value("I like going to the park on weekends.") }
            jsonPath("$.answer.audioUrl") { isNotEmpty() }
            jsonPath("$.answer.durationSeconds") { value(30) }
            jsonPath("$.feedback.estimatedGrade") { value("IM2") }
            jsonPath("$.currentQuestionNumber") { value(2) }
        }
    }

    @Test
    fun `005 AC-1 답변을 제출하면 응답에 답변 분석의 모든 항목이 채워져 있다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-005-ac1")
        val id = createConversation(token)

        submit(token, id, 1).andExpect {
            status { isCreated() }
            jsonPath("$.feedback.estimatedGrade") { isNotEmpty() }
            jsonPath("$.feedback.targetGrade") { value("IH") }
            jsonPath("$.feedback.improvementTips.length()") { value(2) }
            jsonPath("$.feedback.recommendedScript") { isNotEmpty() }
            jsonPath("$.feedback.keyExpressions") { isNotEmpty() }
        }
    }

    @Test
    fun `005 AC-2 답변한 문항의 대화 상세를 다시 조회하면 제출 때와 같은 답변 분석을 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-005-ac2")
        val id = createConversation(token)
        submit(token, id, 1)

        mockMvc.get("/conversations/$id") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.currentQuestionNumber") { value(2) }
            jsonPath("$.questions[0].questionText") { value("Fake question 1") }
            jsonPath("$.questions[0].answer.transcript") { value("I like going to the park on weekends.") }
            jsonPath("$.questions[0].feedback.estimatedGrade") { value("IM2") }
            jsonPath("$.questions[0].feedback.recommendedScript") {
                value("I usually take a slow walk in the park, which helps me unwind.")
            }
            jsonPath("$.questions[1].answer") { value(null) }
        }
    }

    @Test
    fun `AC-3 이미 답한 문항에 다시 제출하면 409 QUESTION_ALREADY_ANSWERED를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-ac3")
        val id = createConversation(token)
        submit(token, id, 1)

        submit(token, id, 1).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("QUESTION_ALREADY_ANSWERED") }
        }
    }

    @Test
    fun `AC-4 현재 문항이 3일 때 5번에 제출하면 409 QUESTION_NOT_CURRENT를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-ac4")
        val id = createConversation(token)
        answerDirectly(id, 1..2)

        submit(token, id, 5).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("QUESTION_NOT_CURRENT") }
        }
    }

    @Test
    fun `AC-6 14개 답한 대화에서 15번에 답하면 현재 문항은 없어지고 대화는 진행중이다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-ac6")
        val id = createConversation(token)
        answerDirectly(id, 1..14)

        submit(token, id, 15).andExpect {
            status { isCreated() }
            jsonPath("$.currentQuestionNumber") { value(null) }
        }

        assertEquals(ConversationStatus.IN_PROGRESS, conversationRepository.findById(id).get().status)
    }

    @Test
    fun `AC-8 허용하지 않는 형식의 파일을 보내면 400 INVALID_AUDIO_FILE을 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-ac8-format")
        val id = createConversation(token)

        submit(token, id, 1, filename = "answer.txt").andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_AUDIO_FILE") }
        }
    }

    @Test
    fun `AC-8 2분이 넘는 음성을 보내면 400 INVALID_AUDIO_FILE을 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "submit-ac8-long")
        val id = createConversation(token)

        submit(token, id, 1, content = FakeSpeechToText.TOO_LONG_AUDIO.toByteArray()).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_AUDIO_FILE") }
        }
    }

    @Test
    fun `AC-7 다른 사용자의 대화에 답변을 제출하면 403을 받는다`() {
        val ownerToken = mockMvc.loginAndGetToken(jsonMapper, "submit-ac7-owner")
        val id = createConversation(ownerToken)
        val otherToken = mockMvc.loginAndGetToken(jsonMapper, "submit-ac7-other")

        submit(otherToken, id, 1).andExpect {
            status { isForbidden() }
            jsonPath("$.code") { value("NOT_CONVERSATION_OWNER") }
        }
    }
}
