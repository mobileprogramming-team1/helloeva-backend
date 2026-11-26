package com.helloeva.domain.user.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.user.domain.User
import com.helloeva.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserUpdater(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun logout(userId: Long) {
        getUser(userId).logout()
    }

    @Transactional
    fun withdraw(userId: Long) {
        getUser(userId).withdraw()
    }

    private fun getUser(userId: Long): User =
        userRepository.findById(userId).orElseThrow { CustomException(ErrorCode.INVALID_TOKEN) }
}
