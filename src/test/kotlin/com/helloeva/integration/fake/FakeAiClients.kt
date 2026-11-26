package com.helloeva.integration.fake

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.integration.question.application.GeneratedQuestions
import com.helloeva.integration.question.application.QuestionGenerator
import com.helloeva.integration.speech.application.SynthesizedSpeech
import com.helloeva.integration.speech.application.TextToSpeech
import com.helloeva.integration.storage.application.FileStorage
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType

class FakeQuestionGenerator : QuestionGenerator {
    override fun generate(targetGrade: Grade, survey: BackgroundSurvey): GeneratedQuestions =
        GeneratedQuestions(
            questions = (1..15).map { "Fake question $it" },
            usage = AiUsage(AiUsageType.QUESTION_GENERATION, "fake-model", 500, 500),
        )
}

class FakeTextToSpeech : TextToSpeech {
    override fun synthesize(text: String): SynthesizedSpeech =
        SynthesizedSpeech(
            audio = text.toByteArray(),
            contentType = "audio/mpeg",
            fileExtension = "mp3",
            durationSeconds = 10,
            usage = AiUsage(AiUsageType.TEXT_TO_SPEECH, "fake-voice", text.length.toLong(), 0),
        )
}

class FakeFileStorage : FileStorage {
    override fun upload(path: String, content: ByteArray, contentType: String): String =
        "https://storage.example.com/$path"
}
