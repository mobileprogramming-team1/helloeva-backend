package com.helloeva.domain.feedback.presentation

import com.helloeva.domain.feedback.domain.AreaScores

data class AreaScoresView(
    val fluency: Int,
    val vocabulary: Int,
    val grammar: Int,
    val organization: Int,
) {
    companion object {
        fun from(scores: AreaScores): AreaScoresView =
            AreaScoresView(scores.fluency, scores.vocabulary, scores.grammar, scores.organization)
    }
}
