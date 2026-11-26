package com.helloeva.integration.question.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.integration.usage.AiUsage

interface QuestionGenerator {
    fun generate(targetGrade: Grade, survey: BackgroundSurvey): GeneratedQuestions
}

data class GeneratedQuestions(
    val questions: List<String>,
    val usage: AiUsage,
)
