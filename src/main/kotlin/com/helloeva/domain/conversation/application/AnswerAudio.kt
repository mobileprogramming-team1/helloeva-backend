package com.helloeva.domain.conversation.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import org.springframework.web.multipart.MultipartFile

class AnswerAudio(
    val bytes: ByteArray,
    val mimeType: String,
    val extension: String,
) {
    companion object {
        private const val MAX_DURATION_SECONDS = 120
        private const val MAX_BYTES = 10L * 1024 * 1024
        private val MIME_TYPES = mapOf(
            "m4a" to "audio/mp4",
            "aac" to "audio/aac",
            "wav" to "audio/wav",
        )

        fun from(file: MultipartFile?): AnswerAudio {
            if (file == null || file.isEmpty || file.size > MAX_BYTES) {
                throw CustomException(ErrorCode.INVALID_AUDIO_FILE)
            }
            val extension = file.originalFilename?.substringAfterLast('.', "")?.lowercase().orEmpty()
            val mimeType = MIME_TYPES[extension] ?: throw CustomException(ErrorCode.INVALID_AUDIO_FILE)
            return AnswerAudio(file.bytes, mimeType, extension)
        }

        fun validateDuration(durationSeconds: Int) {
            if (durationSeconds > MAX_DURATION_SECONDS) throw CustomException(ErrorCode.INVALID_AUDIO_FILE)
        }
    }
}
