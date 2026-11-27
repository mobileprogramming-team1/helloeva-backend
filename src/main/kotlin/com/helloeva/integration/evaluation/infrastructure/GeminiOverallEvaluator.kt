package com.helloeva.integration.evaluation.infrastructure

import com.google.genai.types.Schema
import com.google.genai.types.Type
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.integration.evaluation.application.AnsweredQuestion
import com.helloeva.integration.evaluation.application.OverallEvaluation
import com.helloeva.integration.evaluation.application.OverallEvaluator
import com.helloeva.integration.gemini.GeminiClient
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class GeminiOverallEvaluator(
    private val geminiClient: GeminiClient,
    private val jsonMapper: JsonMapper,
) : OverallEvaluator {

    override fun evaluate(targetGrade: Grade, answers: List<AnsweredQuestion>): OverallEvaluation {
        val result = geminiClient.generateJson(buildPrompt(targetGrade, answers), RESPONSE_SCHEMA, TEMPERATURE)
        val response = jsonMapper.readValue(result.text, EvaluationResponse::class.java)
        return OverallEvaluation(
            overallEstimatedGrade = Grade.valueOf(response.overallEstimatedGrade),
            fluency = response.fluency.coerceIn(0, 100),
            vocabulary = response.vocabulary.coerceIn(0, 100),
            grammar = response.grammar.coerceIn(0, 100),
            organization = response.organization.coerceIn(0, 100),
            strengths = response.strengths,
            weaknesses = response.weaknesses,
            recommendedStudies = response.recommendedStudies,
            frequentExpressions = response.frequentExpressions,
            closingMessage = response.closingMessage,
            usage = AiUsage(AiUsageType.OVERALL_EVALUATION, geminiClient.model, result.inputTokens, result.outputTokens),
        )
    }

    private fun buildPrompt(targetGrade: Grade, answers: List<AnsweredQuestion>): String {
        val transcript = answers.joinToString("\n\n") {
            """
            Q${it.questionNumber}: ${it.questionText}
            A${it.questionNumber}: ${it.transcript.ifBlank { "(no answer)" }}
            Per-question estimate: ${it.estimatedGrade?.name ?: "unknown"}
            """.trimIndent()
        }
        return """
            You are Eva, an experienced OPIc (Oral Proficiency Interview - computer) rater.
            Give the overall result of one full 15-question OPIc mock test taken by a Korean learner.

            OPIc grade scale (low to high): NL < NM < NH < IL < IM1 < IM2 < IM3 < IH < AL
            Target grade: ${targetGrade.name}

            Questions and spoken answers (transcripts may include mistakes and filler words):
            $transcript

            Return:
            - overallEstimatedGrade: the overall OPIc grade for the whole test.
            - fluency, vocabulary, grammar, organization: scores from 0 to 100.
            - strengths: 2 to 4 things done well, in Korean.
            - weaknesses: 2 to 4 things to improve, in Korean.
            - recommendedStudies: 2 to 4 concrete study actions for the next test, in Korean.
            - frequentExpressions: 3 to 8 English words or phrases the learner repeated often, exactly as used.
            - closingMessage: a warm 2-3 sentence closing message from Eva in Korean, reflecting this result and the target grade.
        """.trimIndent()
    }

    private data class EvaluationResponse(
        val overallEstimatedGrade: String = "",
        val fluency: Int = 0,
        val vocabulary: Int = 0,
        val grammar: Int = 0,
        val organization: Int = 0,
        val strengths: List<String> = emptyList(),
        val weaknesses: List<String> = emptyList(),
        val recommendedStudies: List<String> = emptyList(),
        val frequentExpressions: List<String> = emptyList(),
        val closingMessage: String = "",
    )

    companion object {
        private const val TEMPERATURE = 0.3f

        private fun stringList(min: Long, max: Long): Schema =
            Schema.builder()
                .type(Type.Known.ARRAY)
                .items(Schema.builder().type(Type.Known.STRING))
                .minItems(min)
                .maxItems(max)
                .build()

        private fun score(): Schema = Schema.builder().type(Type.Known.INTEGER).minimum(0.0).maximum(100.0).build()

        private val RESPONSE_SCHEMA: Schema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                mapOf(
                    "overallEstimatedGrade" to Schema.builder()
                        .type(Type.Known.STRING)
                        .enum_(Grade.entries.map { it.name })
                        .build(),
                    "fluency" to score(),
                    "vocabulary" to score(),
                    "grammar" to score(),
                    "organization" to score(),
                    "strengths" to stringList(2, 4),
                    "weaknesses" to stringList(2, 4),
                    "recommendedStudies" to stringList(2, 4),
                    "frequentExpressions" to stringList(3, 8),
                    "closingMessage" to Schema.builder().type(Type.Known.STRING).build(),
                ),
            )
            .required(
                "overallEstimatedGrade", "fluency", "vocabulary", "grammar", "organization",
                "strengths", "weaknesses", "recommendedStudies", "frequentExpressions", "closingMessage",
            )
            .build()
    }
}
