package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.Question
import org.springframework.stereotype.Component

@Component
class CurrentQuestionReader(
    private val conversationReader: ConversationReader,
    private val conversationValidator: ConversationValidator,
) {
    fun getCurrentQuestion(conversationId: Long, userId: Long, questionNumber: Int): CurrentQuestion {
        val conversation = conversationReader.getConversation(conversationId)
        conversationValidator.validateOwner(conversation, userId)
        val question = conversationReader.getQuestion(conversation.id, questionNumber)
        conversationValidator.validateCurrentTurn(conversation.id, questionNumber)
        return CurrentQuestion(conversation, question)
    }
}

data class CurrentQuestion(
    val conversation: Conversation,
    val question: Question,
)
