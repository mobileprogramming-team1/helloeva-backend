package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Answer
import com.helloeva.domain.conversation.domain.Question
import com.helloeva.domain.conversation.domain.QuestionFeedback
import com.helloeva.domain.conversation.infrastructure.AnswerRepository
import com.helloeva.domain.conversation.infrastructure.QuestionFeedbackRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class AnswerCreator(
    private val answerRepository: AnswerRepository,
    private val questionFeedbackRepository: QuestionFeedbackRepository,
) {
    @Transactional
    fun create(question: Question, analysis: AnswerAnalysis, audioUrl: String): Pair<Answer, QuestionFeedback> {
        val answer = answerRepository.save(
            Answer(
                question = question,
                transcript = analysis.transcription.transcript,
                audioUrl = audioUrl,
                durationSeconds = analysis.transcription.durationSeconds,
                answeredAt = LocalDateTime.now(),
            ),
        )
        val evaluation = analysis.evaluation
        val feedback = questionFeedbackRepository.save(
            QuestionFeedback(
                answer = answer,
                estimatedGrade = evaluation.estimatedGrade,
                improvementTips = evaluation.improvementTips,
                recommendedScript = evaluation.recommendedScript,
                keyExpressions = evaluation.keyExpressions,
            ),
        )
        return answer to feedback
    }
}
