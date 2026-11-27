package com.helloeva.domain.conversation.presentation

import com.helloeva.common.utils.toSeoulOffset
import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.conversation.domain.Question
import com.helloeva.domain.conversation.domain.QuestionFeedback
import java.time.OffsetDateTime

data class ConversationDetailResponse(
    val conversationId: Long,
    val round: Int,
    val targetGrade: Grade,
    val status: ConversationStatus,
    val totalQuestionCount: Int,
    val currentQuestionNumber: Int?,
    val questions: List<ConversationQuestionItem>,
    val createdAt: OffsetDateTime,
) {
    data class ConversationQuestionItem(
        val questionNumber: Int,
        val questionAudioUrl: String,
        val questionAudioDurationSeconds: Int,
        val questionText: String?,
        val replayRemaining: Int,
        val answer: AnswerView?,
        val feedback: QuestionFeedbackView?,
    )

    companion object {
        fun of(
            conversation: Conversation,
            questions: List<Question>,
            answersByQuestionId: Map<Long, Answer>,
            feedbacksByAnswerId: Map<Long, QuestionFeedback>,
        ): ConversationDetailResponse {
            val answeredNumbers = questions.filter { it.id in answersByQuestionId }.map { it.questionNumber }.toSet()
            return ConversationDetailResponse(
                conversationId = conversation.id,
                round = conversation.round,
                targetGrade = conversation.targetGrade,
                status = conversation.status,
                totalQuestionCount = Conversation.TOTAL_QUESTION_COUNT,
                currentQuestionNumber = Conversation.currentQuestionNumber(answeredNumbers),
                questions = questions.map { question ->
                    val answer = answersByQuestionId[question.id]
                    val feedback = answer?.let { feedbacksByAnswerId[it.id] }
                    ConversationQuestionItem(
                        questionNumber = question.questionNumber,
                        questionAudioUrl = question.audioUrl,
                        questionAudioDurationSeconds = question.audioDurationSeconds,
                        questionText = if (answer != null) question.questionText else null,
                        replayRemaining = question.replayRemaining,
                        answer = answer?.let { AnswerView.from(it) },
                        feedback = feedback?.let { QuestionFeedbackView.of(it, conversation.targetGrade) },
                    )
                },
                createdAt = conversation.createdAt!!.toSeoulOffset(),
            )
        }
    }
}
