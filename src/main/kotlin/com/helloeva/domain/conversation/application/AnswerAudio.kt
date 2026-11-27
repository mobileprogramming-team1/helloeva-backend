package com.helloeva.domain.conversation.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode

class AnswerAudio(
    val bytes: ByteArray,
    val mimeType: String,
    val extension: String,
) {
    companion object {
        private const val MAX_DURATION_SECONDS = 120

        fun validateDuration(durationSeconds: Int) {
            if (durationSeconds > MAX_DURATION_SECONDS) throw CustomException(ErrorCode.INVALID_AUDIO_FILE)
        }
    }
}
