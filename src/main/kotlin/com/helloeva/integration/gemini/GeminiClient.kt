package com.helloeva.integration.gemini

import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Schema
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GeminiClient(
    @Value("\${gcp.project-id}")
    private val projectId: String,
    @Value("\${gcp.vertex.location}")
    private val location: String,
    @Value("\${ai.gemini.model}")
    val model: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val client: Client by lazy {
        Client.builder().vertexAI(true).project(projectId).location(location).build()
    }

    fun generateJson(prompt: String, schema: Schema, temperature: Float): GeminiResult {
        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(schema)
            .temperature(temperature)
            .build()

        var lastError: Exception? = null
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                val response = client.models.generateContent(model, prompt, config)
                val usage = response.usageMetadata().orElse(null)
                return GeminiResult(
                    text = response.text() ?: throw IllegalStateException("Gemini 응답이 비어 있습니다."),
                    inputTokens = usage?.promptTokenCount()?.orElse(0)?.toLong() ?: 0,
                    outputTokens = ((usage?.candidatesTokenCount()?.orElse(0) ?: 0) +
                        (usage?.thoughtsTokenCount()?.orElse(0) ?: 0)).toLong(),
                )
            } catch (e: Exception) {
                lastError = e
                log.warn("[Gemini] 호출 실패 ({}/{}): {}", attempt + 1, MAX_ATTEMPTS, e.message)
            }
        }
        throw IllegalStateException("Gemini 호출에 실패했습니다.", lastError)
    }

    companion object {
        private const val MAX_ATTEMPTS = 3
    }
}

data class GeminiResult(
    val text: String,
    val inputTokens: Long,
    val outputTokens: Long,
)
