package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.integration.evaluation.application.AnswerEvaluation
import com.helloeva.integration.evaluation.application.AnswerEvaluator
import com.helloeva.integration.speech.application.SpeechToText
import com.helloeva.integration.speech.application.Transcription
import com.helloeva.integration.usage.AiUsage
import org.springframework.stereotype.Component

@Component
class AnswerAnalyzer(
    private val speechToText: SpeechToText,
    private val answerEvaluator: AnswerEvaluator,
) {
    fun analyze(questionText: String, targetGrade: Grade, audio: AnswerAudio): AnswerAnalysis {
        val transcription = speechToText.transcribe(audio.bytes, audio.mimeType)
        AnswerAudio.validateDuration(transcription.durationSeconds)
        val evaluation = answerEvaluator.evaluate(questionText, transcription.transcript, targetGrade)
        return AnswerAnalysis(transcription, evaluation)
    }
}

data class AnswerAnalysis(
    val transcription: Transcription,
    val evaluation: AnswerEvaluation,
) {
    val usages: List<AiUsage>
        get() = listOf(transcription.usage, evaluation.usage)
}
