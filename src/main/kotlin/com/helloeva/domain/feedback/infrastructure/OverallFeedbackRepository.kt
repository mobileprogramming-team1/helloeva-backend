package com.helloeva.domain.feedback.infrastructure

import com.helloeva.domain.feedback.domain.OverallFeedback
import org.springframework.data.jpa.repository.JpaRepository

interface OverallFeedbackRepository : JpaRepository<OverallFeedback, Long> {
    fun findByConversationId(conversationId: Long): OverallFeedback?

    fun findAllByConversationIdIn(conversationIds: List<Long>): List<OverallFeedback>
}
