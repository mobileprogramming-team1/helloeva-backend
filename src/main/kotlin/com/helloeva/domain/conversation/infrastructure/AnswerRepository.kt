package com.helloeva.domain.conversation.infrastructure

import com.helloeva.domain.conversation.domain.Answer
import org.springframework.data.jpa.repository.JpaRepository

interface AnswerRepository : JpaRepository<Answer, Long> {
    fun findAllByQuestionConversationId(conversationId: Long): List<Answer>

    fun existsByQuestionId(questionId: Long): Boolean
}
