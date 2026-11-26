package com.helloeva.domain.conversation.application

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.conversation.presentation.CreateConversationRequest
import com.helloeva.domain.survey.domain.BackgroundSurvey

data class CreateConversationCommand(
    val userId: Long,
    val targetGrade: Grade,
    val backgroundSurvey: BackgroundSurvey,
) {
    companion object {
        fun of(userId: Long, request: CreateConversationRequest): CreateConversationCommand =
            CreateConversationCommand(
                userId = userId,
                targetGrade = request.targetGrade!!,
                backgroundSurvey = request.backgroundSurvey!!.toBackgroundSurvey(),
            )
    }
}
