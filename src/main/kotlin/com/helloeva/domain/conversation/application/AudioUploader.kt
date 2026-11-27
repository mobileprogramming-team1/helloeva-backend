package com.helloeva.domain.conversation.application

import com.helloeva.integration.storage.application.FileStorage
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AudioUploader(
    private val fileStorage: FileStorage,
) {
    fun newQuestionFolder(): String = "questions/${UUID.randomUUID()}"

    fun uploadQuestionAudio(folder: String, questionNumber: Int, audio: ByteArray, contentType: String, extension: String): String =
        fileStorage.upload("$folder/$questionNumber.$extension", audio, contentType)

    fun uploadAnswerAudio(questionNumber: Int, audio: AnswerAudio): String =
        fileStorage.upload("answers/${UUID.randomUUID()}/$questionNumber.${audio.extension}", audio.bytes, audio.mimeType)
}
