package com.helloeva.domain.feedback.application

import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.feedback.infrastructure.OverallFeedbackRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class OverallFeedbackReader(
    private val overallFeedbackRepository: OverallFeedbackRepository,
) {
    fun findGradesByConversations(conversations: List<Conversation>): Map<Long, Grade> =
        if (conversations.isEmpty()) {
            emptyMap()
        } else {
            overallFeedbackRepository.findAllByConversationIdIn(conversations.map { it.id })
                .associate { it.conversation.id to it.overallEstimatedGrade }
        }
}
