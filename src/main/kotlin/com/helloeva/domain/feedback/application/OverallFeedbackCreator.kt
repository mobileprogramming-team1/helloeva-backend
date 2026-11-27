package com.helloeva.domain.feedback.application

import com.helloeva.domain.conversation.application.ConversationReader
import com.helloeva.domain.feedback.domain.AreaScores
import com.helloeva.domain.feedback.domain.OverallFeedback
import com.helloeva.domain.feedback.infrastructure.OverallFeedbackRepository
import com.helloeva.integration.evaluation.application.OverallEvaluation
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OverallFeedbackCreator(
    private val overallFeedbackRepository: OverallFeedbackRepository,
    private val conversationReader: ConversationReader,
) {
    @Transactional
    fun create(conversationId: Long, evaluation: OverallEvaluation): OverallFeedback {
        val conversation = conversationReader.getConversation(conversationId)
        val feedback = overallFeedbackRepository.save(
            OverallFeedback(
                conversation = conversation,
                overallEstimatedGrade = evaluation.overallEstimatedGrade,
                areaScores = AreaScores(evaluation.fluency, evaluation.vocabulary, evaluation.grammar, evaluation.organization),
                strengths = evaluation.strengths,
                weaknesses = evaluation.weaknesses,
                recommendedStudies = evaluation.recommendedStudies,
                frequentExpressions = evaluation.frequentExpressions,
                closingMessage = evaluation.closingMessage,
            ),
        )
        conversation.complete()
        return feedback
    }
}
