package com.helloeva.domain.auth.presentation

import com.helloeva.common.security.LoginUser
import com.helloeva.domain.auth.application.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse =
        authService.login(request.kakaoAccessToken!!)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@LoginUser userId: Long) {
        authService.logout(userId)
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdraw(@LoginUser userId: Long) {
        authService.withdraw(userId)
    }
}
