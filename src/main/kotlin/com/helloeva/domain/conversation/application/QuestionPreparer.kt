package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.integration.question.application.QuestionGenerator
import com.helloeva.integration.usage.AiUsage
import org.springframework.stereotype.Component

@Component
class QuestionPreparer(
    private val questionGenerator: QuestionGenerator,
    private val questionAudioCreator: QuestionAudioCreator,
) {
    fun prepare(targetGrade: Grade, backgroundSurvey: BackgroundSurvey): PreparedQuestions {
        val generated = questionGenerator.generate(targetGrade, backgroundSurvey)
        val questionAudios = questionAudioCreator.createAll(generated.questions)
        return PreparedQuestions(questionAudios, listOf(generated.usage) + questionAudios.map { it.usage })
    }
}

data class PreparedQuestions(
    val questionAudios: List<QuestionAudio>,
    val usages: List<AiUsage>,
)
