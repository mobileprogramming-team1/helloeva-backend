package com.helloeva.integration.speech.infrastructure

import com.google.cloud.texttospeech.v1.AudioConfig
import com.google.cloud.texttospeech.v1.AudioEncoding
import com.google.cloud.texttospeech.v1.SynthesisInput
import com.google.cloud.texttospeech.v1.TextToSpeechClient
import com.google.cloud.texttospeech.v1.VoiceSelectionParams
import com.helloeva.integration.speech.application.SynthesizedSpeech
import com.helloeva.integration.speech.application.TextToSpeech
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.math.ceil

@Component
class GoogleTextToSpeech(
    @Value("\${ai.tts.language-code}")
    private val languageCode: String,
    @Value("\${ai.tts.voice-name}")
    private val voiceName: String,
) : TextToSpeech {

    private val log = LoggerFactory.getLogger(javaClass)

    private val clientDelegate = lazy { TextToSpeechClient.create() }
    private val client by clientDelegate

    override fun synthesize(text: String): SynthesizedSpeech {
        val input = SynthesisInput.newBuilder().setText(text).build()
        val voice = VoiceSelectionParams.newBuilder()
            .setLanguageCode(languageCode)
            .setName(voiceName)
            .build()
        val audioConfig = AudioConfig.newBuilder()
            .setAudioEncoding(AudioEncoding.MP3)
            .build()

        var lastError: Exception? = null
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                val audio = client.synthesizeSpeech(input, voice, audioConfig).audioContent.toByteArray()
                return SynthesizedSpeech(
                    audio = audio,
                    contentType = "audio/mpeg",
                    fileExtension = "mp3",
                    durationSeconds = ceil(audio.size / MP3_BYTES_PER_SECOND).toInt(),
                    usage = AiUsage(AiUsageType.TEXT_TO_SPEECH, voiceName, text.length.toLong(), 0),
                )
            } catch (e: Exception) {
                lastError = e
                log.warn("[TTS] 호출 실패 ({}/{}): {}", attempt + 1, MAX_ATTEMPTS, e.message)
            }
        }
        throw IllegalStateException("음성 합성에 실패했습니다.", lastError)
    }

    @PreDestroy
    fun close() {
        if (clientDelegate.isInitialized()) client.close()
    }

    companion object {
        private const val MAX_ATTEMPTS = 3
        private const val MP3_BYTES_PER_SECOND = 8000.0
    }
}
