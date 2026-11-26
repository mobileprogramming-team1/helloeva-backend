package com.helloeva.domain.conversation.infrastructure

import com.helloeva.domain.conversation.domain.QuestionFeedback
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionFeedbackRepository : JpaRepository<QuestionFeedback, Long> {
    fun findAllByAnswerQuestionConversationId(conversationId: Long): List<QuestionFeedback>
}
