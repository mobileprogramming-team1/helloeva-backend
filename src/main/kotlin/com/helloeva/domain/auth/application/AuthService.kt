package com.helloeva.domain.auth.application

import com.helloeva.common.security.JwtTokenProvider
import com.helloeva.domain.auth.presentation.LoginResponse
import com.helloeva.domain.user.application.UserUpdater
import com.helloeva.integration.oauth.application.OauthClient
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oauthClient: OauthClient,
    private val socialUserProcessor: SocialUserProcessor,
    private val userUpdater: UserUpdater,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun login(kakaoAccessToken: String): LoginResponse {
        val userInfo = oauthClient.getUserInfo(kakaoAccessToken)
        val socialUser = socialUserProcessor.getOrCreateUser(userInfo)
        val accessToken = jwtTokenProvider.createAccessToken(socialUser.user.id)
        return LoginResponse.of(accessToken, socialUser)
    }

    fun logout(userId: Long) {
        userUpdater.logout(userId)
    }

    fun withdraw(userId: Long) {
        userUpdater.withdraw(userId)
    }
}
