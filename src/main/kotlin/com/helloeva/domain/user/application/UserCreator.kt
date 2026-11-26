package com.helloeva.domain.user.application

import com.helloeva.domain.user.domain.OauthInfo
import com.helloeva.domain.user.domain.OauthProvider
import com.helloeva.domain.user.domain.User
import com.helloeva.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserCreator(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun create(provider: OauthProvider, providerId: String, nickname: String): User =
        userRepository.save(User(nickname, OauthInfo(provider, providerId)))
}
