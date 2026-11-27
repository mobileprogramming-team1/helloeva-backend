package com.helloeva.domain.conversation.presentation

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.conversation.application.AnswerAudio
import org.springframework.web.multipart.MultipartFile

private const val MAX_BYTES = 10L * 1024 * 1024
private val MIME_TYPES = mapOf(
    "m4a" to "audio/mp4",
    "aac" to "audio/aac",
    "wav" to "audio/wav",
)

fun MultipartFile?.toAnswerAudio(): AnswerAudio {
    if (this == null || isEmpty || size > MAX_BYTES) throw CustomException(ErrorCode.INVALID_AUDIO_FILE)
    val extension = originalFilename?.substringAfterLast('.', "")?.lowercase().orEmpty()
    val mimeType = MIME_TYPES[extension] ?: throw CustomException(ErrorCode.INVALID_AUDIO_FILE)
    return AnswerAudio(bytes, mimeType, extension)
}
