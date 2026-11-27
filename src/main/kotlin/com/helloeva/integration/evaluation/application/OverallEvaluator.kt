package com.helloeva.integration.evaluation.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.integration.usage.AiUsage

interface OverallEvaluator {
    fun evaluate(targetGrade: Grade, answers: List<AnsweredQuestion>): OverallEvaluation
}

data class AnsweredQuestion(
    val questionNumber: Int,
    val questionText: String,
    val transcript: String,
    val estimatedGrade: Grade?,
)

data class OverallEvaluation(
    val overallEstimatedGrade: Grade,
    val fluency: Int,
    val vocabulary: Int,
    val grammar: Int,
    val organization: Int,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val recommendedStudies: List<String>,
    val frequentExpressions: List<String>,
    val closingMessage: String,
    val usage: AiUsage,
)
