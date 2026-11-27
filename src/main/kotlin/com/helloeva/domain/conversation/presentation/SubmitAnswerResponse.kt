package com.helloeva.domain.conversation.presentation

import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.conversation.domain.Question
import com.helloeva.domain.conversation.domain.QuestionFeedback

data class SubmitAnswerResponse(
    val questionNumber: Int,
    val questionText: String,
    val answer: AnswerView,
    val feedback: QuestionFeedbackView,
    val currentQuestionNumber: Int?,
) {
    companion object {
        fun of(
            question: Question,
            answer: Answer,
            feedback: QuestionFeedback,
            targetGrade: Grade,
            currentQuestionNumber: Int?,
        ): SubmitAnswerResponse =
            SubmitAnswerResponse(
                questionNumber = question.questionNumber,
                questionText = question.questionText,
                answer = AnswerView.from(answer),
                feedback = QuestionFeedbackView.of(feedback, targetGrade),
                currentQuestionNumber = currentQuestionNumber,
            )
    }
}
