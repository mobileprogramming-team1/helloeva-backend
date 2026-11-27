package com.helloeva.integration.evaluation.infrastructure

import com.google.genai.types.Schema
import com.google.genai.types.Type
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.integration.evaluation.application.AnswerEvaluation
import com.helloeva.integration.evaluation.application.AnswerEvaluator
import com.helloeva.integration.gemini.GeminiClient
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class GeminiAnswerEvaluator(
    private val geminiClient: GeminiClient,
    private val jsonMapper: JsonMapper,
) : AnswerEvaluator {

    override fun evaluate(questionText: String, transcript: String, targetGrade: Grade): AnswerEvaluation {
        val result = geminiClient.generateJson(buildPrompt(questionText, transcript, targetGrade), RESPONSE_SCHEMA, TEMPERATURE)
        val response = jsonMapper.readValue(result.text, EvaluationResponse::class.java)
        return AnswerEvaluation(
            estimatedGrade = Grade.valueOf(response.estimatedGrade),
            improvementTips = response.improvementTips,
            recommendedScript = response.recommendedScript,
            keyExpressions = response.keyExpressions,
            usage = AiUsage(AiUsageType.ANSWER_EVALUATION, geminiClient.model, result.inputTokens, result.outputTokens),
        )
    }

    private fun buildPrompt(questionText: String, transcript: String, targetGrade: Grade): String =
        """
        You are Eva, an experienced OPIc (Oral Proficiency Interview - computer) rater.
        Evaluate one spoken answer from a Korean test taker.

        OPIc grade scale (low to high): NL < NM < NH < IL < IM1 < IM2 < IM3 < IH < AL
        Target grade: ${targetGrade.name}

        Question: $questionText
        Answer transcript (spoken, may include mistakes and filler words): ${transcript.ifBlank { "(no answer)" }}

        Return:
        - estimatedGrade: the OPIc grade this single answer shows, judged on fluency, vocabulary, grammar, organization and length.
        - improvementTips: 2 to 5 concrete tips in Korean on how to develop this answer toward the target grade. Refer to what the test taker actually said.
        - recommendedScript: an improved English answer at the target grade level, keeping the test taker's own content and ideas. Natural spoken English.
        - keyExpressions: 3 to 6 useful English expressions taken from the recommendedScript.
        """.trimIndent()

    private data class EvaluationResponse(
        val estimatedGrade: String = "",
        val improvementTips: List<String> = emptyList(),
        val recommendedScript: String = "",
        val keyExpressions: List<String> = emptyList(),
    )

    companion object {
        private const val TEMPERATURE = 0.3f

        private val RESPONSE_SCHEMA: Schema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                mapOf(
                    "estimatedGrade" to Schema.builder()
                        .type(Type.Known.STRING)
                        .enum_(Grade.entries.map { it.name })
                        .build(),
                    "improvementTips" to Schema.builder()
                        .type(Type.Known.ARRAY)
                        .items(Schema.builder().type(Type.Known.STRING))
                        .minItems(2)
                        .maxItems(5)
                        .build(),
                    "recommendedScript" to Schema.builder().type(Type.Known.STRING).build(),
                    "keyExpressions" to Schema.builder()
                        .type(Type.Known.ARRAY)
                        .items(Schema.builder().type(Type.Known.STRING))
                        .build(),
                ),
            )
            .required("estimatedGrade", "improvementTips", "recommendedScript", "keyExpressions")
            .build()
    }
}
