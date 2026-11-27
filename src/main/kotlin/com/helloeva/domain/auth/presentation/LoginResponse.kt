package com.helloeva.domain.auth.presentation

import com.helloeva.domain.auth.application.SocialUser

data class LoginResponse(
    val accessToken: String,
    val isNewUser: Boolean,
) {
    companion object {
        fun of(accessToken: String, socialUser: SocialUser): LoginResponse =
            LoginResponse(accessToken = accessToken, isNewUser = socialUser.isNewUser)
    }
}
