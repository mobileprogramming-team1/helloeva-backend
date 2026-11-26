package com.helloeva.domain.feedback.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class AreaScores(
    @Column(nullable = false)
    val fluency: Int,

    @Column(nullable = false)
    val vocabulary: Int,

    @Column(nullable = false)
    val grammar: Int,

    @Column(nullable = false)
    val organization: Int,
) {
    init {
        require(listOf(fluency, vocabulary, grammar, organization).all { it in 0..100 }) {
            "영역별 점수는 0~100이어야 합니다."
        }
    }
}
