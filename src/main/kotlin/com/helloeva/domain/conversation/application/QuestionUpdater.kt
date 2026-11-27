package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Question
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class QuestionUpdater(
    private val currentQuestionReader: CurrentQuestionReader,
) {
    @Transactional
    fun replay(conversationId: Long, userId: Long, questionNumber: Int): Question {
        val question = currentQuestionReader.getCurrentQuestion(conversationId, userId, questionNumber).question
        question.replay()
        return question
    }
}
