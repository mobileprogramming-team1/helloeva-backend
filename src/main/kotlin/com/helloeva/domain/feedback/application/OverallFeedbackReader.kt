package com.helloeva.domain.feedback.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.feedback.domain.OverallFeedback
import com.helloeva.domain.feedback.infrastructure.OverallFeedbackRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class OverallFeedbackReader(
    private val overallFeedbackRepository: OverallFeedbackRepository,
) {
    fun findByConversationId(conversationId: Long): OverallFeedback? =
        overallFeedbackRepository.findByConversationId(conversationId)

    fun getByConversationId(conversationId: Long): OverallFeedback =
        findByConversationId(conversationId) ?: throw CustomException(ErrorCode.OVERALL_FEEDBACK_NOT_REQUESTED)

    fun findGradesByConversations(conversations: List<Conversation>): Map<Long, Grade> =
        if (conversations.isEmpty()) {
            emptyMap()
        } else {
            overallFeedbackRepository.findAllByConversationIdIn(conversations.map { it.id })
                .associate { it.conversation.id to it.overallEstimatedGrade }
        }
}
