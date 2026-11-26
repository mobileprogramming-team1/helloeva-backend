package com.helloeva.domain.auth.application

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.common.security.JwtTokenProvider
import com.helloeva.domain.auth.presentation.LoginResponse
import com.helloeva.domain.user.application.UserCreator
import com.helloeva.domain.user.application.UserReader
import com.helloeva.domain.user.application.UserUpdater
import com.helloeva.domain.user.domain.OauthProvider
import com.helloeva.integration.oauth.application.OauthClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oauthClient: OauthClient,
    private val userReader: UserReader,
    private val userCreator: UserCreator,
    private val userUpdater: UserUpdater,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun login(kakaoAccessToken: String): LoginResponse {
        val userInfo = oauthClient.getUserInfo(kakaoAccessToken)

        val existingUser = userReader.findActiveByProviderId(OauthProvider.KAKAO, userInfo.providerId)
        if (existingUser != null) {
            return LoginResponse(jwtTokenProvider.createAccessToken(existingUser.id), isNewUser = false)
        }

        val nickname = userInfo.nickname
        if (nickname.isNullOrBlank()) {
            log.error("[Kakao] 닉네임 없음. 카카오 동의 항목 설정을 확인하세요. providerId={}", userInfo.providerId)
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        }

        val newUser = userCreator.create(OauthProvider.KAKAO, userInfo.providerId, nickname)
        return LoginResponse(jwtTokenProvider.createAccessToken(newUser.id), isNewUser = true)
    }

    fun logout(userId: Long) {
        userUpdater.logout(userId)
    }

    fun withdraw(userId: Long) {
        userUpdater.withdraw(userId)
    }
}
