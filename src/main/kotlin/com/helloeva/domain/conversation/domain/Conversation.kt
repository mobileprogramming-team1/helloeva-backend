package com.helloeva.domain.conversation.domain

import com.helloeva.common.domain.BaseEntity
import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.domain.user.domain.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "conversations")
class Conversation(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val round: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val targetGrade: Grade,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val backgroundSurvey: BackgroundSurvey,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ConversationStatus = ConversationStatus.IN_PROGRESS
        protected set

    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.PERSIST])
    @OrderBy("questionNumber ASC")
    val questions: MutableList<Question> = mutableListOf()

    val isCompleted: Boolean
        get() = status == ConversationStatus.COMPLETED

    fun validateOwner(userId: Long) {
        if (user.id != userId) throw CustomException(ErrorCode.NOT_CONVERSATION_OWNER)
    }

    fun complete() {
        status = ConversationStatus.COMPLETED
    }
}
