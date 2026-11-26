package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Conversation
import com.helloeva.domain.conversation.domain.Question
import com.helloeva.domain.conversation.infrastructure.ConversationRepository
import com.helloeva.domain.user.application.UserReader
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ConversationCreator(
    private val conversationRepository: ConversationRepository,
    private val userReader: UserReader,
) {
    @Transactional
    fun create(command: CreateConversationCommand, questionAudios: List<QuestionAudio>): Conversation {
        val user = userReader.getById(command.userId)
        val round = conversationRepository.countByUserId(command.userId).toInt() + 1
        val conversation = Conversation(user, round, command.targetGrade, command.backgroundSurvey)
        questionAudios.forEach {
            conversation.questions.add(
                Question(conversation, it.questionNumber, it.text, it.audioUrl, it.durationSeconds),
            )
        }
        return conversationRepository.save(conversation)
    }
}
