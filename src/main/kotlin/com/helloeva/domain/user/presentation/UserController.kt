package com.helloeva.domain.user.presentation

import com.helloeva.common.security.LoginUser
import com.helloeva.domain.user.application.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getMe(@LoginUser userId: Long): MeResponse =
        userService.getMe(userId)
}
