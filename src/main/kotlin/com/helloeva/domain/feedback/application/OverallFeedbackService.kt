package com.helloeva.domain.feedback.application

import com.helloeva.domain.conversation.application.ConversationReader
import com.helloeva.domain.conversation.application.ConversationValidator
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.feedback.presentation.CreateOverallFeedbackResponse
import com.helloeva.domain.feedback.presentation.OverallFeedbackResponse
import org.springframework.stereotype.Service

@Service
class OverallFeedbackService(
    private val conversationReader: ConversationReader,
    private val conversationValidator: ConversationValidator,
    private val overallFeedbackReader: OverallFeedbackReader,
    private val overallFeedbackGenerator: OverallFeedbackGenerator,
) {
    fun create(conversationId: Long, userId: Long): CreateOverallFeedbackResult {
        val conversation = conversationReader.getConversation(conversationId)
        conversationValidator.validateOwner(conversation, userId)
        val generated = overallFeedbackGenerator.getOrGenerate(conversation)
        return CreateOverallFeedbackResult.of(conversation, generated)
    }

    fun get(conversationId: Long, userId: Long): OverallFeedbackResponse {
        val conversation = conversationReader.getConversation(conversationId)
        conversationValidator.validateOwner(conversation, userId)
        val feedback = overallFeedbackReader.getByConversationId(conversation.id)
        return OverallFeedbackResponse.of(conversation, feedback)
    }
}

data class CreateOverallFeedbackResult(
    val response: CreateOverallFeedbackResponse,
    val created: Boolean,
) {
    companion object {
        fun of(conversation: Conversation, generated: GeneratedOverallFeedback): CreateOverallFeedbackResult =
            CreateOverallFeedbackResult(CreateOverallFeedbackResponse.of(conversation, generated.feedback), generated.created)
    }
}
