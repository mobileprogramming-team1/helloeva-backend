package com.helloeva.integration.speech.infrastructure

import com.google.genai.types.Schema
import com.google.genai.types.Type
import com.helloeva.integration.gemini.GeminiClient
import com.helloeva.integration.speech.application.SpeechToText
import com.helloeva.integration.speech.application.Transcription
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import kotlin.math.ceil

@Component
class GeminiSpeechToText(
    private val geminiClient: GeminiClient,
    private val jsonMapper: JsonMapper,
) : SpeechToText {

    override fun transcribe(audio: ByteArray, mimeType: String): Transcription {
        val result = geminiClient.generateJsonFromAudio(PROMPT, audio, mimeType, RESPONSE_SCHEMA, TEMPERATURE)
        val transcript = jsonMapper.readValue(result.text, TranscriptResponse::class.java).transcript.trim()
        return Transcription(
            transcript = transcript,
            durationSeconds = ceil(result.audioInputTokens / AUDIO_TOKENS_PER_SECOND).toInt(),
            usage = AiUsage(
                type = AiUsageType.SPEECH_TO_TEXT,
                model = geminiClient.model,
                inputUnits = result.inputTokens,
                outputUnits = result.outputTokens,
                audioInputUnits = result.audioInputTokens,
            ),
        )
    }

    private data class TranscriptResponse(
        val transcript: String = "",
    )

    companion object {
        private const val TEMPERATURE = 0f
        private const val AUDIO_TOKENS_PER_SECOND = 25.0

        private val PROMPT = """
            Transcribe this English speaking-test answer exactly as spoken.
            - Keep grammar mistakes, wrong words, repetitions and filler words (um, uh) as they are. Do not correct or polish anything.
            - Do not add punctuation that changes meaning, and do not summarize.
            - If nothing intelligible is spoken, return an empty transcript.
        """.trimIndent()

        private val RESPONSE_SCHEMA: Schema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(mapOf("transcript" to Schema.builder().type(Type.Known.STRING).build()))
            .required("transcript")
            .build()
    }
}
