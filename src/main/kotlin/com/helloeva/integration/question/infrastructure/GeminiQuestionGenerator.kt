package com.helloeva.integration.question.infrastructure

import com.google.genai.types.Schema
import com.google.genai.types.Type
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.integration.gemini.GeminiClient
import com.helloeva.integration.question.application.GeneratedQuestions
import com.helloeva.integration.question.application.QuestionGenerator
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class GeminiQuestionGenerator(
    private val geminiClient: GeminiClient,
    private val jsonMapper: JsonMapper,
) : QuestionGenerator {

    override fun generate(targetGrade: Grade, survey: BackgroundSurvey): GeneratedQuestions {
        var inputTokens = 0L
        var outputTokens = 0L

        repeat(MAX_ATTEMPTS) {
            val result = geminiClient.generateJson(buildPrompt(targetGrade, survey), RESPONSE_SCHEMA, TEMPERATURE)
            inputTokens += result.inputTokens
            outputTokens += result.outputTokens

            val generated = jsonMapper.readValue(result.text, QuestionsResponse::class.java)
                .questions
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (generated.size == GENERATED_COUNT) {
                return GeneratedQuestions(
                    questions = listOf(SELF_INTRODUCTION) + generated,
                    usage = AiUsage(AiUsageType.QUESTION_GENERATION, geminiClient.model, inputTokens, outputTokens),
                )
            }
        }
        throw IllegalStateException("질문 ${GENERATED_COUNT}개를 생성하지 못했습니다.")
    }

    private fun buildPrompt(targetGrade: Grade, survey: BackgroundSurvey): String =
        """
        You are Eva, an OPIc (Oral Proficiency Interview - computer) examiner.
        Write questions 2 to 15 of one OPIc mock test in English. Question 1 (self-introduction) is already fixed.

        Test taker's target grade: ${targetGrade.name} (scale: NL < NM < NH < IL < IM1 < IM2 < IM3 < IH < AL)
        Background survey answers:
        - Job: ${survey.job.name}
        - Student: ${survey.student.name}
        - Recent course: ${survey.recentCourse.name}
        - Residence: ${survey.residence.name}
        - Leisure activities: ${survey.leisures.joinToString { it.name }}
        - Hobbies and interests: ${survey.hobbies.joinToString { it.name }}
        - Sports: ${survey.sports.joinToString { it.name }}
        - Vacations and trips: ${survey.travels.joinToString { it.name }}

        Follow the real OPIc structure, in this exact order (14 questions):
        - Questions 2-4: a combo on one topic picked from the survey (describe it, then routine or past experience, then a memorable experience).
        - Questions 5-7: a combo on a different topic picked from the survey, same pattern.
        - Questions 8-10: a combo on an unexpected topic NOT in the survey (e.g. technology, weather, recycling, banks, transportation), same pattern.
        - Questions 11-13: a role-play combo. 11: ask the examiner three or four questions about a situation. 12: solve a problem in a related situation. 13: describe a similar past experience.
        - Questions 14-15: advanced questions on one topic, such as comparing past and present or discussing a social issue or change.

        Rules:
        - Speak directly to the test taker as the examiner, in natural spoken English, 2 to 4 sentences each.
        - Survey-based questions may start like "You indicated in the survey that ...".
        - Match vocabulary and complexity to the target grade: simple and concrete for NL-IL, moderate for IM1-IM3, detailed and abstract for IH-AL.
        - Do not number the questions.
        """.trimIndent()

    private data class QuestionsResponse(
        val questions: List<String> = emptyList(),
    )

    companion object {
        const val SELF_INTRODUCTION = "Let's start the interview now. Tell me a little bit about yourself."
        private const val GENERATED_COUNT = 14
        private const val MAX_ATTEMPTS = 2
        private const val TEMPERATURE = 0.9f

        private val RESPONSE_SCHEMA: Schema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                mapOf(
                    "questions" to Schema.builder()
                        .type(Type.Known.ARRAY)
                        .items(Schema.builder().type(Type.Known.STRING))
                        .minItems(GENERATED_COUNT.toLong())
                        .maxItems(GENERATED_COUNT.toLong())
                        .build(),
                ),
            )
            .required("questions")
            .build()
    }
}
