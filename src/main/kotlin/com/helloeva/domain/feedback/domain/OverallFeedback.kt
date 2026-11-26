package com.helloeva.domain.feedback.domain

import com.helloeva.common.domain.BaseEntity
import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.Grade
import jakarta.persistence.Column
import jakarta.persistence.Embedded
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
@Table(name = "overall_feedbacks")
class OverallFeedback(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false, unique = true)
    val conversation: Conversation,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val overallEstimatedGrade: Grade,

    @Embedded
    val areaScores: AreaScores,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val strengths: List<String>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val weaknesses: List<String>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val recommendedStudies: List<String>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val frequentExpressions: List<String>,

    @Column(nullable = false, columnDefinition = "TEXT")
    val closingMessage: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
