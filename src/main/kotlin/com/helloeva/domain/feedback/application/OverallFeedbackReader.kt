package com.helloeva.domain.feedback.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.feedback.infrastructure.OverallFeedbackRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class OverallFeedbackReader(
    private val overallFeedbackRepository: OverallFeedbackRepository,
) {
    fun findGradesByConversationIds(conversationIds: List<Long>): Map<Long, Grade> =
        if (conversationIds.isEmpty()) {
            emptyMap()
        } else {
            overallFeedbackRepository.findAllByConversationIdIn(conversationIds)
                .associate { it.conversation.id to it.overallEstimatedGrade }
        }
}
