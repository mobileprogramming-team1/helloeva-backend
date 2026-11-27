package com.helloeva.domain.conversation.presentation

import com.helloeva.common.security.LoginUser
import com.helloeva.domain.conversation.application.AnswerAudio
import com.helloeva.domain.conversation.application.AnswerService
import com.helloeva.domain.conversation.application.ConversationService
import com.helloeva.domain.conversation.application.CreateConversationCommand
import com.helloeva.domain.conversation.domain.ConversationStatus
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/conversations")
class ConversationController(
    private val conversationService: ConversationService,
    private val answerService: AnswerService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @LoginUser userId: Long,
        @Valid @RequestBody request: CreateConversationRequest,
    ): CreateConversationResponse =
        conversationService.create(CreateConversationCommand.of(userId, request))

    @GetMapping("/{conversationId}")
    fun getConversation(
        @LoginUser userId: Long,
        @PathVariable conversationId: Long,
    ): ConversationDetailResponse =
        conversationService.getConversation(conversationId, userId)

    @PostMapping("/{conversationId}/questions/{questionNumber}/replay")
    fun replay(
        @LoginUser userId: Long,
        @PathVariable conversationId: Long,
        @PathVariable questionNumber: Int,
    ): ReplayResponse =
        conversationService.replay(conversationId, userId, questionNumber)

    @PostMapping("/{conversationId}/questions/{questionNumber}/answer", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun submitAnswer(
        @LoginUser userId: Long,
        @PathVariable conversationId: Long,
        @PathVariable questionNumber: Int,
        @RequestPart("audio", required = false) audio: MultipartFile?,
    ): SubmitAnswerResponse =
        answerService.submit(conversationId, userId, questionNumber, AnswerAudio.from(audio))

    @GetMapping
    fun getConversations(
        @LoginUser userId: Long,
        @RequestParam(required = false) status: ConversationStatus?,
    ): ConversationListResponse =
        conversationService.getConversations(userId, status)
}
