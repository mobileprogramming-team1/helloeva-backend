package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.presentation.SubmitAnswerResponse
import com.helloeva.domain.usage.application.ApiUsageRecorder
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val currentQuestionReader: CurrentQuestionReader,
    private val conversationReader: ConversationReader,
    private val answerAnalyzer: AnswerAnalyzer,
    private val audioUploader: AudioUploader,
    private val answerCreator: AnswerCreator,
    private val apiUsageRecorder: ApiUsageRecorder,
) {
    fun submit(conversationId: Long, userId: Long, questionNumber: Int, audio: AnswerAudio): SubmitAnswerResponse {
        val (conversation, question) = currentQuestionReader.getCurrentQuestion(conversationId, userId, questionNumber)
        val analysis = answerAnalyzer.analyze(question.questionText, conversation.targetGrade, audio)
        val audioUrl = audioUploader.uploadAnswerAudio(questionNumber, audio)
        val (answer, feedback) = answerCreator.create(question, analysis, audioUrl)
        apiUsageRecorder.record(conversation.id, analysis.usages)
        return SubmitAnswerResponse.of(
            question = question,
            answer = answer,
            feedback = feedback,
            targetGrade = conversation.targetGrade,
            currentQuestionNumber = conversationReader.findCurrentQuestionNumber(conversation.id),
        )
    }
}
