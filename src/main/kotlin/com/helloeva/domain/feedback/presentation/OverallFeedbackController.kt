package com.helloeva.domain.feedback.presentation

import com.helloeva.common.security.LoginUser
import com.helloeva.domain.feedback.application.OverallFeedbackService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/conversations/{conversationId}/overall-feedback")
class OverallFeedbackController(
    private val overallFeedbackService: OverallFeedbackService,
) {
    @PostMapping
    fun create(
        @LoginUser userId: Long,
        @PathVariable conversationId: Long,
    ): ResponseEntity<CreateOverallFeedbackResponse> {
        val result = overallFeedbackService.create(conversationId, userId)
        val status = if (result.created) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity.status(status).body(result.response)
    }

    @GetMapping
    fun get(
        @LoginUser userId: Long,
        @PathVariable conversationId: Long,
    ): OverallFeedbackResponse =
        overallFeedbackService.get(conversationId, userId)
}
