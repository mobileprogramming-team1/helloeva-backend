package com.helloeva.integration.fake

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.integration.evaluation.application.AnswerEvaluation
import com.helloeva.integration.evaluation.application.AnswerEvaluator
import com.helloeva.integration.evaluation.application.AnsweredQuestion
import com.helloeva.integration.evaluation.application.OverallEvaluation
import com.helloeva.integration.evaluation.application.OverallEvaluator
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

class FakeOverallEvaluator : OverallEvaluator {
    override fun evaluate(targetGrade: Grade, answers: List<AnsweredQuestion>): OverallEvaluation =
        OverallEvaluation(
            overallEstimatedGrade = Grade.IM3,
            fluency = 70,
            vocabulary = 65,
            grammar = 60,
            organization = 75,
            strengths = listOf("질문에 맞는 답변을 끝까지 이어 말했어요.", "일상 주제에서 어휘가 자연스러웠어요."),
            weaknesses = listOf("시제 일치가 아직 불안정해요.", "같은 표현을 반복해요."),
            recommendedStudies = listOf("연결어 표현 정리하기", "주제별 핵심 표현 10개씩 정리하기"),
            frequentExpressions = listOf("very", "a lot", "really"),
            closingMessage = "오늘 정말 고생 많았어요. 다음 모의고사에서 또 만나요!",
            usage = AiUsage(AiUsageType.OVERALL_EVALUATION, "fake-model", 3000, 500),
        )
}

class FakeFileStorage : FileStorage {
    override fun upload(path: String, content: ByteArray, contentType: String): String =
        "https://storage.example.com/$path"
}
