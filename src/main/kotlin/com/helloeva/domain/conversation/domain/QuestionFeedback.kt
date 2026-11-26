package com.helloeva.domain.conversation.domain

import com.helloeva.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "question_feedbacks")
class QuestionFeedback(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    val answer: Answer,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val estimatedGrade: Grade,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val improvementTips: List<String>,

    @Column(nullable = false, columnDefinition = "TEXT")
    val recommendedScript: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val keyExpressions: List<String>,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
