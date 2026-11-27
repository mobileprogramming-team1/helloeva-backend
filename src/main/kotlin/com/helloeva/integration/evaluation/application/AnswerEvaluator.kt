package com.helloeva.integration.evaluation.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.integration.usage.AiUsage

interface AnswerEvaluator {
    fun evaluate(questionText: String, transcript: String, targetGrade: Grade): AnswerEvaluation
}

data class AnswerEvaluation(
    val estimatedGrade: Grade,
    val improvementTips: List<String>,
    val recommendedScript: String,
    val keyExpressions: List<String>,
    val usage: AiUsage,
)
