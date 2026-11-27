package com.helloeva.domain.conversation.presentation

import com.helloeva.common.utils.toSeoulOffset
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.domain.Grade
import java.time.OffsetDateTime

data class ConversationListResponse(
    val totalCount: Int,
    val items: List<ConversationSummary>,
) {
    data class ConversationSummary(
        val conversationId: Long,
        val round: Int,
        val targetGrade: Grade,
        val status: ConversationStatus,
        val overallEstimatedGrade: Grade?,
        val createdAt: OffsetDateTime,
    )

    companion object {
        fun of(conversations: List<Conversation>, overallGrades: Map<Long, Grade>): ConversationListResponse =
            ConversationListResponse(
                totalCount = conversations.size,
                items = conversations.map {
                    ConversationSummary(
                        conversationId = it.id,
                        round = it.round,
                        targetGrade = it.targetGrade,
                        status = it.status,
                        overallEstimatedGrade = overallGrades[it.id],
                        createdAt = it.createdAt!!.toSeoulOffset(),
                    )
                },
            )
    }
}
