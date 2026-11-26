package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.presentation.CreateConversationResponse
import com.helloeva.domain.usage.application.ApiUsageRecorder
import com.helloeva.integration.question.application.QuestionGenerator
import org.springframework.stereotype.Service

@Service
class ConversationService(
    private val questionGenerator: QuestionGenerator,
    private val questionAudioCreator: QuestionAudioCreator,
    private val conversationCreator: ConversationCreator,
    private val apiUsageRecorder: ApiUsageRecorder,
) {
    fun create(command: CreateConversationCommand): CreateConversationResponse {
        val generated = questionGenerator.generate(command.targetGrade, command.backgroundSurvey)
        val questionAudios = questionAudioCreator.createAll(generated.questions)
        val conversation = conversationCreator.create(command, questionAudios)

        apiUsageRecorder.record(conversation.id, listOf(generated.usage) + questionAudios.map { it.usage })

        return CreateConversationResponse(conversation.id)
    }
}
