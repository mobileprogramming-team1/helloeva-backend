package com.helloeva.domain.conversation.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.domain.Question
import com.helloeva.domain.conversation.domain.QuestionFeedback
import com.helloeva.domain.conversation.infrastructure.AnswerRepository
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import com.helloeva.domain.conversation.infrastructure.QuestionFeedbackRepository
import com.helloeva.domain.conversation.infrastructure.QuestionRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ConversationReader(
    private val conversationRepository: ConversationRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val questionFeedbackRepository: QuestionFeedbackRepository,
) {
    fun getConversation(conversationId: Long): Conversation =
        conversationRepository.findById(conversationId)
            .orElseThrow { CustomException(ErrorCode.CONVERSATION_NOT_FOUND) }

    fun getQuestion(conversationId: Long, questionNumber: Int): Question =
        questionRepository.findByConversationIdAndQuestionNumber(conversationId, questionNumber)
            ?: throw CustomException(ErrorCode.QUESTION_NOT_FOUND)

    fun findAnsweredQuestionNumbers(conversationId: Long): Set<Int> =
        answerRepository.findAnsweredQuestionNumbers(conversationId).toSet()

    fun findCurrentQuestionNumber(conversationId: Long): Int? {
        val answeredNumbers = findAnsweredQuestionNumbers(conversationId)
        return (1..Conversation.TOTAL_QUESTION_COUNT).firstOrNull { it !in answeredNumbers }
    }

    fun findQuestions(conversationId: Long): List<Question> =
        questionRepository.findAllByConversationIdOrderByQuestionNumberAsc(conversationId)

    fun findAnswersByQuestionId(conversationId: Long): Map<Long, Answer> =
        answerRepository.findAllByQuestionConversationId(conversationId).associateBy { it.question.id }

    fun findFeedbacksByAnswerId(conversationId: Long): Map<Long, QuestionFeedback> =
        questionFeedbackRepository.findAllByAnswerQuestionConversationId(conversationId).associateBy { it.answer.id }

    fun findAllByUser(userId: Long, status: ConversationStatus?): List<Conversation> =
        if (status == null) {
            conversationRepository.findAllByUserIdOrderByRoundDesc(userId)
        } else {
            conversationRepository.findAllByUserIdAndStatusOrderByRoundDesc(userId, status)
        }
}
