package com.helloeva.domain.feedback.application

import com.helloeva.domain.conversation.application.ConversationReader
import com.helloeva.integration.evaluation.application.AnsweredQuestion
import org.springframework.stereotype.Component

@Component
class AnsweredQuestionsCollector(
    private val conversationReader: ConversationReader,
) {
    fun collect(conversationId: Long): List<AnsweredQuestion> {
        val answersByQuestionId = conversationReader.findAnswersByQuestionId(conversationId)
        val feedbacksByAnswerId = conversationReader.findFeedbacksByAnswerId(conversationId)
        return conversationReader.findQuestions(conversationId).mapNotNull { question ->
            val answer = answersByQuestionId[question.id] ?: return@mapNotNull null
            AnsweredQuestion(
                questionNumber = question.questionNumber,
                questionText = question.questionText,
                transcript = answer.transcript,
                estimatedGrade = feedbacksByAnswerId[answer.id]?.estimatedGrade,
            )
        }
    }
}
