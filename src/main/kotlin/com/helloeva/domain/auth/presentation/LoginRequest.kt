package com.helloeva.domain.auth.presentation

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "필수 값입니다.")
    val kakaoAccessToken: String?,
)
