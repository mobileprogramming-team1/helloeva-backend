package com.helloeva.domain.conversation.infrastructure

import com.helloeva.domain.conversation.domain.Answer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AnswerRepository : JpaRepository<Answer, Long> {
    fun findAllByQuestionConversationId(conversationId: Long): List<Answer>

    fun existsByQuestionId(questionId: Long): Boolean

    @Query("select a.question.questionNumber from Answer a where a.question.conversation.id = :conversationId")
    fun findAnsweredQuestionNumbers(conversationId: Long): List<Int>
}
