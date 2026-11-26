package com.helloeva.domain.user.application

import com.helloeva.domain.user.presentation.MeResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userReader: UserReader,
) {
    fun getMe(userId: Long): MeResponse =
        MeResponse.from(userReader.getById(userId))
}
