package com.helloeva.integration.speech.application

import com.helloeva.integration.usage.AiUsage

interface TextToSpeech {
    fun synthesize(text: String): SynthesizedSpeech
}

data class SynthesizedSpeech(
    val audio: ByteArray,
    val contentType: String,
    val fileExtension: String,
    val durationSeconds: Int,
    val usage: AiUsage,
)
