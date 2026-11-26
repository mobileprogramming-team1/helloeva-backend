package com.helloeva.integration.usage

data class AiUsage(
    val type: AiUsageType,
    val model: String,
    val inputUnits: Long,
    val outputUnits: Long,
)

enum class AiUsageType(val description: String) {
    QUESTION_GENERATION("질문 생성 (입력·출력 토큰)"),
    ANSWER_EVALUATION("답변 평가 (입력·출력 토큰)"),
    OVERALL_EVALUATION("종합 평가 (입력·출력 토큰)"),
    TEXT_TO_SPEECH("음성 합성 (입력 글자 수)"),
    SPEECH_TO_TEXT("음성 인식 (입력 초)"),
}
