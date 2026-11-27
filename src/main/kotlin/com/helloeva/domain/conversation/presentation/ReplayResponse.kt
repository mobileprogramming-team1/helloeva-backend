package com.helloeva.domain.conversation.presentation

import com.helloeva.domain.conversation.domain.Question

data class ReplayResponse(
    val questionNumber: Int,
    val replayRemaining: Int,
    val questionAudioUrl: String,
) {
    companion object {
        fun from(question: Question): ReplayResponse =
            ReplayResponse(
                questionNumber = question.questionNumber,
                replayRemaining = question.replayRemaining,
                questionAudioUrl = question.audioUrl,
            )
    }
}
