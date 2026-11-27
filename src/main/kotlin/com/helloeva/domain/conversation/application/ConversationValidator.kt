package com.helloeva.domain.conversation.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.conversation.domain.Conversation
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ConversationValidator(
    private val conversationReader: ConversationReader,
) {
    fun validateOwner(conversation: Conversation, userId: Long) {
        if (conversation.user.id != userId) {
            throw CustomException(ErrorCode.NOT_CONVERSATION_OWNER)
        }
    }

    fun validateAllAnswered(conversationId: Long) {
        if (conversationReader.findCurrentQuestionNumber(conversationId) != null) {
            throw CustomException(ErrorCode.QUESTIONS_NOT_ALL_ANSWERED)
        }
    }

    fun validateCurrentTurn(conversationId: Long, questionNumber: Int) {
        val answeredNumbers = conversationReader.findAnsweredQuestionNumbers(conversationId)
        if (questionNumber in answeredNumbers) {
            throw CustomException(ErrorCode.QUESTION_ALREADY_ANSWERED)
        }
        if (questionNumber != conversationReader.findCurrentQuestionNumber(conversationId)) {
            throw CustomException(ErrorCode.QUESTION_NOT_CURRENT)
        }
    }
}
