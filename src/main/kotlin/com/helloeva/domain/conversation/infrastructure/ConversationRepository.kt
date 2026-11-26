package com.helloeva.domain.conversation.infrastructure

import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.ConversationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationRepository : JpaRepository<Conversation, Long> {
    fun countByUserId(userId: Long): Long

    fun findAllByUserIdOrderByRoundDesc(userId: Long): List<Conversation>

    fun findAllByUserIdAndStatusOrderByRoundDesc(userId: Long, status: ConversationStatus): List<Conversation>
}
