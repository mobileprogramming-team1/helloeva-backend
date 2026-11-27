package com.helloeva.domain.conversation.presentation

import com.helloeva.common.utils.toSeoulOffset
import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.conversation.domain.QuestionFeedback
import java.time.OffsetDateTime

data class AnswerView(
    val transcript: String,
    val audioUrl: String,
    val durationSeconds: Int,
    val answeredAt: OffsetDateTime,
) {
    companion object {
        fun from(answer: Answer): AnswerView =
            AnswerView(
                transcript = answer.transcript,
                audioUrl = answer.audioUrl,
                durationSeconds = answer.durationSeconds,
                answeredAt = answer.answeredAt.toSeoulOffset(),
            )
    }
}

data class QuestionFeedbackView(
    val estimatedGrade: Grade,
    val targetGrade: Grade,
    val improvementTips: List<String>,
    val recommendedScript: String,
    val keyExpressions: List<String>,
) {
    companion object {
        fun of(feedback: QuestionFeedback, targetGrade: Grade): QuestionFeedbackView =
            QuestionFeedbackView(
                estimatedGrade = feedback.estimatedGrade,
                targetGrade = targetGrade,
                improvementTips = feedback.improvementTips,
                recommendedScript = feedback.recommendedScript,
                keyExpressions = feedback.keyExpressions,
            )
    }
}
