package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.ConversationStatus
import com.helloeva.domain.conversation.presentation.ConversationDetailResponse
import com.helloeva.domain.conversation.presentation.ConversationListResponse
import com.helloeva.domain.conversation.presentation.CreateConversationResponse
import com.helloeva.domain.conversation.presentation.ReplayResponse
import com.helloeva.domain.feedback.application.OverallFeedbackReader
import com.helloeva.domain.usage.application.ApiUsageRecorder
import org.springframework.stereotype.Service

@Service
class ConversationService(
    private val questionPreparer: QuestionPreparer,
    private val conversationCreator: ConversationCreator,
    private val apiUsageRecorder: ApiUsageRecorder,
    private val conversationReader: ConversationReader,
    private val conversationValidator: ConversationValidator,
    private val overallFeedbackReader: OverallFeedbackReader,
    private val questionUpdater: QuestionUpdater,
) {
    fun create(command: CreateConversationCommand): CreateConversationResponse {
        val prepared = questionPreparer.prepare(command.targetGrade, command.backgroundSurvey)
        val conversation = conversationCreator.create(command, prepared.questionAudios)
        apiUsageRecorder.record(conversation.id, prepared.usages)
        return CreateConversationResponse.from(conversation)
    }

    fun getConversation(conversationId: Long, userId: Long): ConversationDetailResponse {
        val conversation = conversationReader.getConversation(conversationId)
        conversationValidator.validateOwner(conversation, userId)
        return ConversationDetailResponse.of(
            conversation = conversation,
            questions = conversationReader.findQuestions(conversationId),
            answersByQuestionId = conversationReader.findAnswersByQuestionId(conversationId),
            feedbacksByAnswerId = conversationReader.findFeedbacksByAnswerId(conversationId),
            currentQuestionNumber = conversationReader.findCurrentQuestionNumber(conversationId),
        )
    }

    fun replay(conversationId: Long, userId: Long, questionNumber: Int): ReplayResponse =
        ReplayResponse.from(questionUpdater.replay(conversationId, userId, questionNumber))

    fun getConversations(userId: Long, status: ConversationStatus?): ConversationListResponse {
        val conversations = conversationReader.findAllByUser(userId, status)
        val overallGrades = overallFeedbackReader.findGradesByConversations(conversations)
        return ConversationListResponse.of(conversations, overallGrades)
    }
}
