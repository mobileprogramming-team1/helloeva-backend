package com.helloeva.integration.domain.conversation

import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import com.helloeva.domain.usage.infrastructure.ApiUsageRepository
import com.helloeva.integration.IntegrationTest
import com.helloeva.integration.loginAndGetToken
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Transactional
class CreateConversationTest : IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Autowired
    lateinit var conversationRepository: ConversationRepository

    @Autowired
    lateinit var apiUsageRepository: ApiUsageRepository

    private fun survey(
        leisures: String = """["MOVIES", "PARKS", "CAFES", "SHOPPING"]""",
        sports: String = """["JOGGING", "WALKING", "GYM"]""",
    ) = """
        {
          "job": "OFFICE_WORKER",
          "student": "NO",
          "recentCourse": "OVER_FIVE_YEARS_AGO",
          "residence": "ALONE",
          "leisures": $leisures,
          "hobbies": ["LISTENING_TO_MUSIC", "READING"],
          "sports": $sports,
          "travels": ["STAYCATION", "DOMESTIC_TRAVEL", "OVERSEAS_TRAVEL"]
        }
    """.trimIndent()

    private fun create(token: String, survey: String = survey(), targetGrade: String = "\"IM3\"") =
        mockMvc.post("/conversations") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{"targetGrade": $targetGrade, "backgroundSurvey": $survey}"""
        }

    private fun createdId(token: String): Long {
        val body = create(token).andReturn().response.contentAsString
        return jsonMapper.readTree(body)["conversationId"].asLong()
    }

    @Test
    fun `AC-1 대화를 만들면 회차가 붙은 진행중 대화가 15문항과 질문 음성으로 생성된다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "create-ac1")

        create(token).andExpect {
            status { isCreated() }
            jsonPath("$.conversationId") { isNumber() }
        }

        val conversation = conversationRepository.findAll().last()
        assertEquals(1, conversation.round)
        assertEquals(ConversationStatus.IN_PROGRESS, conversation.status)
        assertEquals((1..15).toList(), conversation.questions.map { it.questionNumber })
        assertTrue(conversation.questions.first().audioUrl.isNotBlank())
        assertEquals(16, apiUsageRepository.findAll().count { it.conversationId == conversation.id })
    }

    @Test
    fun `AC-2 여가·취미·운동·휴가 합계가 12개 미만이면 400과 필드 오류를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "create-ac2")

        create(token, survey(leisures = """["MOVIES", "PARKS"]""", sports = """["JOGGING"]""")).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_INPUT_VALUE") }
            jsonPath("$.errors[0].field") { value("backgroundSurvey") }
        }
    }

    @Test
    fun `AC-3 여가 활동을 1개만 고르면 400과 필드 오류를 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "create-ac3")

        create(token, survey(leisures = """["MOVIES"]""")).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_INPUT_VALUE") }
            jsonPath("$.errors[?(@.field == 'backgroundSurvey.leisures')]") { exists() }
        }
    }

    @Test
    fun `AC-4 없는 선택지 코드를 보내면 400을 받는다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "create-ac4")

        create(token, survey(leisures = """["MOVIES", "PARKS", "SKYDIVING"]""")).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_INPUT_VALUE") }
        }
    }

    @Test
    fun `AC-5 대화가 3개 있는 사용자가 새로 만들면 회차는 4다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "create-ac5")
        repeat(3) { createdId(token) }

        val id = createdId(token)

        assertEquals(4, conversationRepository.findById(id).get().round)
    }

    @Test
    fun `AC-6 운동을 전혀 하지 않음과 걷기를 함께 골라도 생성된다`() {
        val token = mockMvc.loginAndGetToken(jsonMapper, "create-ac6")

        create(token, survey(sports = """["NONE", "WALKING", "YOGA"]""")).andExpect {
            status { isCreated() }
        }
    }
}
