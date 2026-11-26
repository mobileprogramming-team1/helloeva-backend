package com.helloeva.domain.conversation.domain

import com.helloeva.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "answers")
class Answer(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    val question: Question,

    @Column(nullable = false, columnDefinition = "TEXT")
    val transcript: String,

    @Column(nullable = false, length = 500)
    val audioUrl: String,

    @Column(nullable = false)
    val durationSeconds: Int,

    @Column(nullable = false)
    val answeredAt: LocalDateTime,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
