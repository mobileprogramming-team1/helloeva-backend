package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ConversationReader(
    private val conversationRepository: ConversationRepository,
) {
    fun findAllByUser(userId: Long, status: ConversationStatus?): List<Conversation> =
        if (status == null) {
            conversationRepository.findAllByUserIdOrderByRoundDesc(userId)
        } else {
            conversationRepository.findAllByUserIdAndStatusOrderByRoundDesc(userId, status)
        }
}
