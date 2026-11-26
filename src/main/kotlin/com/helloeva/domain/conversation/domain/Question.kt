package com.helloeva.domain.conversation.domain

import com.helloeva.common.domain.BaseEntity
import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "questions")
class Question(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    val conversation: Conversation,

    @Column(nullable = false)
    val questionNumber: Int,

    @Column(nullable = false, columnDefinition = "TEXT")
    val questionText: String,

    @Column(nullable = false, length = 500)
    val audioUrl: String,

    @Column(nullable = false)
    val audioDurationSeconds: Int,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false)
    var replayCount: Int = 0
        protected set

    val replayRemaining: Int
        get() = MAX_REPLAY_COUNT - replayCount

    fun replay() {
        if (replayCount >= MAX_REPLAY_COUNT) throw CustomException(ErrorCode.REPLAY_LIMIT_EXCEEDED)
        replayCount++
    }

    companion object {
        const val MAX_REPLAY_COUNT = 1
    }
}
