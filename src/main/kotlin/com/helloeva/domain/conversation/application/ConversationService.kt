package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.presentation.ConversationListResponse
import com.helloeva.domain.conversation.presentation.CreateConversationResponse
import com.helloeva.domain.feedback.application.OverallFeedbackReader
import com.helloeva.domain.usage.application.ApiUsageRecorder
import com.helloeva.integration.question.application.QuestionGenerator
import org.springframework.stereotype.Service

@Service
class ConversationService(
    private val questionGenerator: QuestionGenerator,
    private val questionAudioCreator: QuestionAudioCreator,
    private val conversationCreator: ConversationCreator,
    private val apiUsageRecorder: ApiUsageRecorder,
    private val conversationReader: ConversationReader,
    private val overallFeedbackReader: OverallFeedbackReader,
) {
    fun create(command: CreateConversationCommand): CreateConversationResponse {
        val generated = questionGenerator.generate(command.targetGrade, command.backgroundSurvey)
        val questionAudios = questionAudioCreator.createAll(generated.questions)
        val conversation = conversationCreator.create(command, questionAudios)

        apiUsageRecorder.record(conversation.id, listOf(generated.usage) + questionAudios.map { it.usage })

        return CreateConversationResponse(conversation.id)
    }

    fun getConversations(userId: Long, status: ConversationStatus?): ConversationListResponse {
        val conversations = conversationReader.findAllByUser(userId, status)
        val overallGrades = overallFeedbackReader.findGradesByConversationIds(conversations.map { it.id })
        return ConversationListResponse.of(conversations, overallGrades)
    }
}
