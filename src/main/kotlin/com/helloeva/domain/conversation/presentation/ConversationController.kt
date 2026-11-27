package com.helloeva.domain.conversation.presentation

import com.helloeva.common.security.LoginUser
import com.helloeva.domain.conversation.application.ConversationService
import com.helloeva.domain.conversation.application.CreateConversationCommand
import com.helloeva.domain.conversation.domain.ConversationStatus
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/conversations")
class ConversationController(
    private val conversationService: ConversationService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @LoginUser userId: Long,
        @Valid @RequestBody request: CreateConversationRequest,
    ): CreateConversationResponse =
        conversationService.create(CreateConversationCommand.of(userId, request))

    @GetMapping
    fun getConversations(
        @LoginUser userId: Long,
        @RequestParam(required = false) status: ConversationStatus?,
    ): ConversationListResponse =
        conversationService.getConversations(userId, status)
}
