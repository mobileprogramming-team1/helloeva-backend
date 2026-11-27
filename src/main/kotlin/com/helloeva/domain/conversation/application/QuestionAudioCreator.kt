package com.helloeva.domain.conversation.application

import com.helloeva.integration.speech.application.TextToSpeech
import com.helloeva.integration.usage.AiUsage
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class QuestionAudioCreator(
    private val textToSpeech: TextToSpeech,
    private val audioUploader: AudioUploader,
) {
    fun createAll(questionTexts: List<String>): List<QuestionAudio> {
        val folder = audioUploader.newQuestionFolder()
        return Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            questionTexts
                .mapIndexed { index, text -> executor.submit<QuestionAudio> { create(folder, index + 1, text) } }
                .map { it.get() }
        }
    }

    private fun create(folder: String, questionNumber: Int, text: String): QuestionAudio {
        val speech = textToSpeech.synthesize(text)
        val url = audioUploader.uploadQuestionAudio(folder, questionNumber, speech.audio, speech.contentType, speech.fileExtension)
        return QuestionAudio(
            questionNumber = questionNumber,
            text = text,
            audioUrl = url,
            durationSeconds = speech.durationSeconds,
            usage = speech.usage,
        )
    }
}

data class QuestionAudio(
    val questionNumber: Int,
    val text: String,
    val audioUrl: String,
    val durationSeconds: Int,
    val usage: AiUsage,
)
