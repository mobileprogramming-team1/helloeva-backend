package com.helloeva.domain.conversation.infrastructure

import com.helloeva.domain.conversation.domain.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<Question, Long> {
    fun findByConversationIdAndQuestionNumber(conversationId: Long, questionNumber: Int): Question?

    fun findAllByConversationIdOrderByQuestionNumberAsc(conversationId: Long): List<Question>
}
