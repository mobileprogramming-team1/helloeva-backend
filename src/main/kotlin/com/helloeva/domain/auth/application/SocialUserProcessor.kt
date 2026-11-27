package com.helloeva.domain.auth.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.user.application.UserCreator
import com.helloeva.domain.user.application.UserReader
import com.helloeva.domain.user.domain.OauthProvider
import com.helloeva.domain.user.domain.User
import com.helloeva.integration.oauth.application.OauthUserInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SocialUserProcessor(
    private val userReader: UserReader,
    private val userCreator: UserCreator,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getOrCreateUser(userInfo: OauthUserInfo): SocialUser {
        val existing = userReader.findActiveByProviderId(OauthProvider.KAKAO, userInfo.providerId)
        if (existing != null) return SocialUser(existing, isNewUser = false)
        return SocialUser(create(userInfo), isNewUser = true)
    }

    private fun create(userInfo: OauthUserInfo): User {
        val nickname = userInfo.nickname
        if (nickname.isNullOrBlank()) {
            log.error("[Kakao] 닉네임 없음. 카카오 동의 항목 설정을 확인하세요. providerId={}", userInfo.providerId)
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        }
        return userCreator.create(OauthProvider.KAKAO, userInfo.providerId, nickname)
    }
}

data class SocialUser(
    val user: User,
    val isNewUser: Boolean,
)
