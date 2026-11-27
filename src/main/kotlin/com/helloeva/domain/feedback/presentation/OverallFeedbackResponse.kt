package com.helloeva.domain.feedback.presentation

import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.feedback.domain.OverallFeedback

data class OverallFeedbackResponse(
    val conversationId: Long,
    val totalQuestionCount: Int,
    val overallEstimatedGrade: Grade,
    val targetGrade: Grade,
    val areaScores: AreaScoresView,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val recommendedStudies: List<String>,
    val frequentExpressions: List<String>,
    val closingMessage: String,
) {
    companion object {
        fun of(conversation: Conversation, feedback: OverallFeedback): OverallFeedbackResponse =
            OverallFeedbackResponse(
                conversationId = conversation.id,
                totalQuestionCount = Conversation.TOTAL_QUESTION_COUNT,
                overallEstimatedGrade = feedback.overallEstimatedGrade,
                targetGrade = conversation.targetGrade,
                areaScores = AreaScoresView.from(feedback.areaScores),
                strengths = feedback.strengths,
                weaknesses = feedback.weaknesses,
                recommendedStudies = feedback.recommendedStudies,
                frequentExpressions = feedback.frequentExpressions,
                closingMessage = feedback.closingMessage,
            )
    }
}
