package com.helloeva.integration.fake

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.integration.evaluation.application.AnswerEvaluation
import com.helloeva.integration.evaluation.application.AnswerEvaluator
import com.helloeva.integration.question.application.GeneratedQuestions
import com.helloeva.integration.question.application.QuestionGenerator
import com.helloeva.integration.speech.application.SpeechToText
import com.helloeva.integration.speech.application.SynthesizedSpeech
import com.helloeva.integration.speech.application.TextToSpeech
import com.helloeva.integration.speech.application.Transcription
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

class FakeSpeechToText : SpeechToText {
    override fun transcribe(audio: ByteArray, mimeType: String): Transcription =
        Transcription(
            transcript = "I like going to the park on weekends.",
            durationSeconds = if (String(audio).startsWith(TOO_LONG_AUDIO)) 150 else 30,
            usage = AiUsage(AiUsageType.SPEECH_TO_TEXT, "fake-model", 1000, 50, 960),
        )

    companion object {
        const val TOO_LONG_AUDIO = "TOO_LONG"
    }
}

class FakeAnswerEvaluator : AnswerEvaluator {
    override fun evaluate(questionText: String, transcript: String, targetGrade: Grade): AnswerEvaluation =
        AnswerEvaluation(
            estimatedGrade = Grade.IM2,
            improvementTips = listOf("공원의 분위기를 구체적으로 묘사해 보세요.", "연결어를 써서 문장을 이어 보세요."),
            recommendedScript = "I usually take a slow walk in the park, which helps me unwind.",
            keyExpressions = listOf("slow walk", "unwind"),
            usage = AiUsage(AiUsageType.ANSWER_EVALUATION, "fake-model", 300, 200),
        )
}

class FakeFileStorage : FileStorage {
    override fun upload(path: String, content: ByteArray, contentType: String): String =
        "https://storage.example.com/$path"
}
