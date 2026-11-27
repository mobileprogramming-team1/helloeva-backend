package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Question
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class QuestionUpdater(
    private val conversationReader: ConversationReader,
    private val conversationValidator: ConversationValidator,
) {
    @Transactional
    fun replay(conversationId: Long, userId: Long, questionNumber: Int): Question {
        val conversation = conversationReader.getConversation(conversationId)
        conversationValidator.validateOwner(conversation, userId)
        val question = conversationReader.getQuestion(conversation.id, questionNumber)
        conversationValidator.validateCurrentTurn(conversation.id, questionNumber)
        question.replay()
        return question
    }
}
