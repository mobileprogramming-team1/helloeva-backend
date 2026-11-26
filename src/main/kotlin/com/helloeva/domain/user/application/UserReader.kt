package com.helloeva.domain.user.application

import com.helloeva.domain.user.domain.OauthProvider
import com.helloeva.domain.user.domain.User
import com.helloeva.domain.user.domain.UserStatus
import com.helloeva.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class UserReader(
    private val userRepository: UserRepository,
) {
    fun findActiveByProviderId(provider: OauthProvider, providerId: String): User? =
        userRepository.findByOauthInfoProviderAndOauthInfoProviderIdAndStatus(provider, providerId, UserStatus.ACTIVE)
}
