package com.helloeva.integration.speech.application

import com.helloeva.integration.usage.AiUsage

interface SpeechToText {
    fun transcribe(audio: ByteArray, mimeType: String): Transcription
}

data class Transcription(
    val transcript: String,
    val durationSeconds: Int,
    val usage: AiUsage,
)
