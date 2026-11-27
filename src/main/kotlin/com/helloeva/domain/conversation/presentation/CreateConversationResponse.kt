package com.helloeva.domain.conversation.presentation

import com.helloeva.domain.conversation.domain.Conversation

data class CreateConversationResponse(
    val conversationId: Long,
) {
    companion object {
        fun from(conversation: Conversation): CreateConversationResponse =
            CreateConversationResponse(conversation.id)
    }
}
