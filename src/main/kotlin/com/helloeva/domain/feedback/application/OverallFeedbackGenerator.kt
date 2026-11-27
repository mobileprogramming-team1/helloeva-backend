package com.helloeva.domain.feedback.application

import com.helloeva.domain.conversation.application.ConversationValidator
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.feedback.domain.OverallFeedback
import com.helloeva.domain.usage.application.ApiUsageRecorder
import com.helloeva.integration.evaluation.application.OverallEvaluator
import org.springframework.stereotype.Component

@Component
class OverallFeedbackGenerator(
    private val overallFeedbackReader: OverallFeedbackReader,
    private val conversationValidator: ConversationValidator,
    private val answeredQuestionsCollector: AnsweredQuestionsCollector,
    private val overallEvaluator: OverallEvaluator,
    private val overallFeedbackCreator: OverallFeedbackCreator,
    private val apiUsageRecorder: ApiUsageRecorder,
) {
    fun getOrGenerate(conversation: Conversation): GeneratedOverallFeedback {
        val existing = overallFeedbackReader.findByConversationId(conversation.id)
        if (existing != null) return GeneratedOverallFeedback(existing, created = false)
        return GeneratedOverallFeedback(generate(conversation), created = true)
    }

    private fun generate(conversation: Conversation): OverallFeedback {
        conversationValidator.validateAllAnswered(conversation.id)
        val evaluation = overallEvaluator.evaluate(conversation.targetGrade, answeredQuestionsCollector.collect(conversation.id))
        val feedback = overallFeedbackCreator.create(conversation.id, evaluation)
        apiUsageRecorder.record(conversation.id, listOf(evaluation.usage))
        return feedback
    }
}

data class GeneratedOverallFeedback(
    val feedback: OverallFeedback,
    val created: Boolean,
)
