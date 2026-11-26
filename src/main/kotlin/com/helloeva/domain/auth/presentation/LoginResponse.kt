package com.helloeva.domain.auth.presentation

data class LoginResponse(
    val accessToken: String,
    val isNewUser: Boolean,
)
